package com.lms.performance;

import com.lms.attendance.AttendanceRecord;
import com.lms.attendance.AttendanceRepository;
import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.assignment.submission.AssignmentSubmissionRepository;
import com.lms.user.Role;
import com.lms.user.User;
import com.lms.user.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final AttendanceRepository attendanceRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final UserRepository userRepository;

    @Autowired
    public PerformanceService(AttendanceRepository attendanceRepository,
                              AssignmentSubmissionRepository assignmentSubmissionRepository,
                              UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.userRepository = userRepository;
    }

    public byte[] generateGradesExcelReport(Long courseId) {
        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .toList(); // Fetch only users with student role
        Map<Long, Double> grades = calculateGrades(courseId);

        List<List<String>> rows = students.stream()
                .map(student -> List.of(
                        String.valueOf(student.getId()),
                        student.getName(),
                        grades.get((long) student.getId()).toString()
                ))
                .collect(Collectors.toList());

        rows.add(0, List.of("Student ID", "Student Name", "Grade"));
        return generateExcelFile("Grades Report", rows, "Scores Chart", "Student Name", "Scores");
    }

    public byte[] generateAttendanceExcelReport(Long courseId) {
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findByIdCourseId(courseId);

        Map<Long, String> studentNames = fetchStudentNames();

        List<Long> lessonIds = attendanceRecords.stream()
                .map(record -> record.getId().getLessonId())
                .distinct()
                .sorted()
                .toList();

        Map<Long, List<AttendanceRecord>> attendanceByStudent = attendanceRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getId().getStudentId()));

        List<String> header = new ArrayList<>();
        header.add("Student Name");
        lessonIds.forEach(lessonId -> header.add("Lesson " + lessonId));
        header.add("Total (%)");

        List<List<String>> rows = new ArrayList<>();
        rows.add(header); // Add the header row

        for (Map.Entry<Long, List<AttendanceRecord>> entry : attendanceByStudent.entrySet()) {
            Long studentId = entry.getKey();
            List<AttendanceRecord> studentRecords = entry.getValue();

            List<String> row = new ArrayList<>();
            row.add(studentNames.getOrDefault(studentId, "Unknown Student"));

            Map<Long, Boolean> lessonAttendance = studentRecords.stream()
                    .collect(Collectors.toMap(
                            record -> record.getId().getLessonId(),
                            AttendanceRecord::isAttended
                    ));

            int attendedCount = 0;
            for (Long lessonId : lessonIds) {
                boolean attended = lessonAttendance.getOrDefault(lessonId, false);
                row.add(attended ? "YES" : "NO");
                if (attended) {
                    attendedCount++;
                }
            }

            double totalPercentage = (!lessonIds.isEmpty()) ? ((double) attendedCount / lessonIds.size()) * 100 : 0.0;
            row.add(String.format("%.2f %%", totalPercentage));

            rows.add(row);
        }

        return generateExcelFile("Attendance Report", rows, "Attendance Chart", "Student Name", "Attendance (%)");
    }

    private Map<Long, String> fetchStudentNames() {
        return userRepository.findAll().stream()
                .collect(Collectors.toMap(
                        user -> (long) user.getId(),
                        User::getName
                ));
    }

    private Map<Long, Double> calculateGrades(Long courseId) {
        List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findAllByAssignmentId(courseId);

        return submissions.stream()
                .filter(submission -> submission.getScore() != null)
                .collect(Collectors.groupingBy(
                        submission -> (long) submission.getStudent().getId(),
                        Collectors.averagingDouble(AssignmentSubmission::getScore)
                ));
    }

    private byte[] generateExcelFile(String sheetName, List<List<String>> data, String chartTitle, String xAxisTitle, String yAxisTitle) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                List<String> rowData = data.get(rowIndex);

                for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
                    Cell cell = row.createCell(colIndex);
                    cell.setCellValue(rowData.get(colIndex));

                    if (rowIndex == 0) {
                        cell.setCellStyle(headerStyle);
                    }
                }
            }

            addBarChart(sheet, data.size(), data.get(0).size(), chartTitle, xAxisTitle, yAxisTitle);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    private void addBarChart(Sheet sheet, int rowCount, int columnCount, String chartTitle, String xAxisTitle, String yAxisTitle) {
        // Create a drawing canvas on the sheet
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();

        // Define the position of the chart on the sheet
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, columnCount + 2, 1, columnCount + 8, 15);

        // Create the chart
        XDDFChart chart = drawing.createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        // Define the data range for the chart
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(xAxisTitle);

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(yAxisTitle);
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        // Define the data sources for the chart
        XDDFDataSource<String> xAxisData = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet,
                new CellRangeAddress(1, rowCount - 1, 0, 0)); // Student names
        XDDFNumericalDataSource<Double> yAxisData = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                new CellRangeAddress(1, rowCount - 1, columnCount - 1, columnCount - 1)); // Scores or percentages

        // Create the chart data
        XDDFBarChartData barChartData = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFBarChartData.Series series = (XDDFBarChartData.Series) barChartData.addSeries(xAxisData, yAxisData);
        series.setTitle(sheet.getRow(0).getCell(columnCount - 1).getStringCellValue(), null); // Set the series title

        // Customize bar chart appearance
        barChartData.setBarDirection(BarDirection.COL);
        barChartData.setBarGrouping(BarGrouping.CLUSTERED);

        // Plot the chart
        chart.plot(barChartData);
    }
}
