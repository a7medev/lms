package com.lms.performance;

import com.lms.attendance.AttendanceRecord;
import com.lms.attendance.AttendanceRepository;
import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.assignment.submission.AssignmentSubmissionRepository;
import com.lms.quiz.quizsubmission.QuizSubmission;
import com.lms.quiz.quizsubmission.QuizSubmissionRepository;
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
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    @Autowired
    public PerformanceService(AttendanceRepository attendanceRepository,
                              AssignmentSubmissionRepository assignmentSubmissionRepository,
                              QuizSubmissionRepository quizSubmissionRepository,
                              UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.userRepository = userRepository;
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
        rows.add(header);

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

    public byte[] generateGradesExcelReport(Long courseId) {
        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .toList(); // Fetch only users with student role
        Map<Long, Double> assignmentGrades = calculateAssignmentGrades(courseId);
        Map<Long, Double> quizGrades = calculateQuizGrades(courseId);
        List<List<String>> rows = students.stream()
                .filter(student -> assignmentGrades.containsKey((long) student.getId()) || quizGrades.containsKey((long) student.getId()))
                .map(student -> List.of(
                        String.valueOf(student.getId()),
                        student.getName(),
                        assignmentGrades.getOrDefault((long) student.getId(), 0.0).toString(),
                        quizGrades.getOrDefault((long) student.getId(), 0.0).toString(),
                        String.valueOf(assignmentGrades.getOrDefault((long) student.getId(), 0.0) + quizGrades.getOrDefault((long) student.getId(), 0.0))
                ))
                .collect(Collectors.toList());

        rows.add(0, List.of("Student ID", "Student Name", "Assignment Grade", "Quiz Grade", "Total"));
        return generateExcelFile("Grades Report", rows, "Scores Chart", "Student Name", "Scores");
    }

    private Map<Long, Double> calculateAssignmentGrades(Long courseId) {
        List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findAllByAssignmentId(courseId);
        return submissions.stream()
                .filter(submission -> submission.getScore() != null)
                .collect(Collectors.groupingBy(
                        submission -> (long) submission.getStudent().getId(),
                        Collectors.averagingDouble(AssignmentSubmission::getScore)
                ));
    }
    private Map<Long, Double> calculateQuizGrades(Long courseId) {
        List<QuizSubmission> quizSubmissions = quizSubmissionRepository.findAll().stream()
                .filter(submission -> submission.getQuiz().getCourse().getCourseId().equals(courseId))
                .toList();
        return quizSubmissions.stream()
                .collect(Collectors.groupingBy(
                        submission -> (long) submission.getStudent().getId(),
                        Collectors.averagingDouble(QuizSubmission::getMarks)
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

            if (sheetName.equals("Grades Report")) {
                addGradesBarChart(sheet, data.size(), data.get(0).size(), chartTitle, xAxisTitle, yAxisTitle);
            }
            else if (sheetName.equals("Attendance Report")) {
                addAttendancePieChart(sheet, data.size(), data.get(0).size(), chartTitle);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    private void addGradesBarChart(Sheet sheet, int rowCount, int columnCount, String chartTitle, String xAxisTitle, String yAxisTitle) {
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, columnCount + 2, 1, columnCount + 8, 15);

        XDDFChart chart = drawing.createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(xAxisTitle);

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(yAxisTitle);
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFDataSource<String> xAxisData = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet, new CellRangeAddress(1, rowCount - 1, 1, 1));
        XDDFNumericalDataSource<Double> yAxisData = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(1, rowCount - 1, 4, 4));

        XDDFBarChartData barChartData = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFBarChartData.Series series = (XDDFBarChartData.Series) barChartData.addSeries(xAxisData, yAxisData);
        series.setTitle("Total Grade", null);
        
        barChartData.setBarDirection(BarDirection.COL);
        barChartData.setBarGrouping(BarGrouping.CLUSTERED);

        chart.plot(barChartData);
    }


    private void addAttendancePieChart(Sheet sheet, int rowCount, int columnCount, String chartTitle) {
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, columnCount + 2, 1, columnCount + 8, 15);

        XDDFChart chart = drawing.createChart(anchor);
        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);

        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet,
                new CellRangeAddress(1, rowCount - 1, 0, 0)); // Student names
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                new CellRangeAddress(1, rowCount - 1, columnCount - 1, columnCount - 1)); // Attendance percentages

        XDDFPieChartData data = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
        XDDFPieChartData.Series series = (XDDFPieChartData.Series) data.addSeries(categories, values);
        series.setTitle(chartTitle, null);

        chart.plot(data);
    }

}
