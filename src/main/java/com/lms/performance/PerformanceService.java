package com.lms.performance;

import com.lms.attendance.AttendanceRecord;
import com.lms.attendance.AttendanceRepository;
import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.assignment.submission.AssignmentSubmissionRepository;
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

    private final AttendanceRepository attendanceRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    // quizSubmissionRepository to be added later
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
        List<User> students = userRepository.findAll(); // Fetch all students
        Map<Long, Double> grades = calculateGrades(courseId);

        List<List<String>> rows = students.stream()
                .map(student -> List.of(
                        String.valueOf(student.getId()),
                        student.getName(),
                        grades.getOrDefault((long) student.getId(), 0.0).toString()
                ))
                .collect(Collectors.toList());

        rows.add(0, List.of("Student ID", "Student Name", "Grade"));
        return generateExcelFile("Grades Report", rows);
    }

    public byte[] generateAttendanceExcelReport(Long courseId) {
        // Fetch attendance records for the course
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findByIdCourseId(courseId);

        // Fetch student names
        Map<Long, String> studentNames = fetchStudentNames();

        // Get distinct lesson IDs and sort them to ensure consistent order
        List<Long> lessonIds = attendanceRecords.stream()
                .map(record -> record.getId().getLessonId())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Group attendance records by student
        Map<Long, List<AttendanceRecord>> attendanceByStudent = attendanceRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getId().getStudentId()));

        // Build the header row: "Student Name, Lesson 1, Lesson 2, ..., Total"
        List<String> header = new ArrayList<>();
        header.add("Student Name");
        lessonIds.forEach(lessonId -> header.add("Lesson " + lessonId));
        header.add("Total (%)");

        // Build data rows
        List<List<String>> rows = new ArrayList<>();
        rows.add(header); // Add the header row

        for (Map.Entry<Long, List<AttendanceRecord>> entry : attendanceByStudent.entrySet()) {
            Long studentId = entry.getKey();
            List<AttendanceRecord> studentRecords = entry.getValue();

            // Initialize row with student name
            List<String> row = new ArrayList<>();
            row.add(studentNames.getOrDefault(studentId, "Unknown Student"));

            // Map lesson attendance status (true = attended, false = not attended)
            Map<Long, Boolean> lessonAttendance = studentRecords.stream()
                    .collect(Collectors.toMap(
                            record -> record.getId().getLessonId(),
                            AttendanceRecord::isAttended
                    ));

            // Fill row with attendance status for each lesson
            int attendedCount = 0;
            for (Long lessonId : lessonIds) {
                boolean attended = lessonAttendance.getOrDefault(lessonId, false);
                row.add(attended ? "✔" : "✘"); // Use checkmarks for clarity
                if (attended) {
                    attendedCount++;
                }
            }

            // Add total percentage
            double totalPercentage = (lessonIds.size() > 0) ? ((double) attendedCount / lessonIds.size()) * 100 : 0.0;
            row.add(String.format("%.2f %%", totalPercentage));

            rows.add(row);
        }

        return generateExcelFile("Attendance Report", rows);
    }

    private Map<Long, String> fetchStudentNames() {
        // Fetch all users and map their IDs to names
        return userRepository.findAll().stream()
                .collect(Collectors.toMap(
                        user -> (long) user.getId(), // Convert `int` ID to `Long` to match other mappings
                        User::getName
                ));
    }


    private Map<Long, Double> calculateGrades(Long courseId) {
        List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findAllByAssignmentId(courseId);

        // Aggregate scores by student
        Map<Long, List<AssignmentSubmission>> submissionsByStudent = submissions.stream()
                .collect(Collectors.groupingBy(AssignmentSubmission::getStudentId));

        Map<Long, Double> averageGrades = new HashMap<>();
        submissionsByStudent.forEach((studentId, studentSubmissions) -> {
            double totalScore = studentSubmissions.stream()
                    .filter(submission -> submission.getScore() != null) // Only consider graded submissions
                    .mapToDouble(AssignmentSubmission::getScore)
                    .sum();
            long gradedSubmissions = studentSubmissions.stream()
                    .filter(submission -> submission.getScore() != null)
                    .count();
            double averageScore = (gradedSubmissions > 0) ? totalScore / gradedSubmissions : 0.0;
            averageGrades.put(studentId, averageScore);
        });

        return averageGrades;
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
