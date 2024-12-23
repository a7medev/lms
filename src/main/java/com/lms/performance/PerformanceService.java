package com.lms.performance;

import com.lms.attendance.AttendanceRecord;
import com.lms.attendance.AttendanceRepository;
import com.lms.user.User;
import com.lms.user.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Autowired
    public PerformanceService(PerformanceRepository performanceRepository,
                              AttendanceRepository attendanceRepository,
                              UserRepository userRepository) {
        this.performanceRepository = performanceRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public byte[] generateGradesExcelReport(Long courseId) {
        List<Performance> gradesData = performanceRepository.findAllByCourseId(courseId);

        Map<Long, String> studentNames = fetchStudentNames();

        List<List<String>> rows = gradesData.stream()
                .map(performance -> List.of(
                        performance.getStudentId().toString(),
                        studentNames.getOrDefault(performance.getStudentId(), "Unknown Student"),
                        performance.getScore().toString()
                ))
                .collect(Collectors.toList());

        rows.add(0, List.of("Student ID", "Student Name", "Grade"));

        return generateExcelFile("Grades Report", rows);
    }

    public byte[] generateAttendanceExcelReport(Long courseId) {
        Map<Long, Double> attendancePercentages = calculateAttendancePercentages(courseId);

        List<Performance> attendanceData = performanceRepository.findAllByCourseId(courseId);

        Map<Long, String> studentNames = fetchStudentNames();

        List<List<String>> rows = attendanceData.stream()
                .map(performance -> List.of(
                        performance.getStudentId().toString(),
                        studentNames.getOrDefault(performance.getStudentId(), "Unknown Student"),
                        attendancePercentages.getOrDefault(performance.getStudentId(), 0.0) + " %"
                ))
                .collect(Collectors.toList());

        rows.add(0, List.of("Student ID", "Student Name", "Attendance (%)"));

        return generateExcelFile("Attendance Report", rows);
    }

    private Map<Long, Double> calculateAttendancePercentages(Long courseId) {
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findByIdCourseId(courseId);

        Map<Long, List<AttendanceRecord>> attendanceByStudent = attendanceRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getId().getStudentId()));

        Map<Long, Double> attendancePercentages = new HashMap<>();
        attendanceByStudent.forEach((studentId, records) -> {
            long attendedLessons = records.stream().filter(AttendanceRecord::isAttended).count();
            long totalLessons = records.size();
            double percentage = (totalLessons > 0) ? ((double) attendedLessons / totalLessons) * 100 : 0.0;
            attendancePercentages.put(studentId, percentage);
        });

        return attendancePercentages;
    }

    private Map<Long, String> fetchStudentNames() {
        List<User> students = userRepository.findAll();
        return students.stream()
                .collect(Collectors.toMap(
                        user -> (long) user.getId(), // Convert `int` ID to `Long` to match other mappings
                        User::getName
                ));
    }

    private byte[] generateExcelFile(String sheetName, List<List<String>> data) {
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
                        cell.setCellStyle(headerStyle); // Style for headers
                    }
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }
}
