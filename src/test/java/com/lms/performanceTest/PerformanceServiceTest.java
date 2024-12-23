package com.lms.performanceTest;

import com.lms.LmsApplication;
import com.lms.attendance.AttendanceRecord;
import com.lms.attendance.AttendanceRepository;
import com.lms.attendance.AttendanceKey;
import com.lms.assignment.submission.AssignmentSubmission;
import com.lms.assignment.submission.AssignmentSubmissionRepository;
import com.lms.performance.PerformanceService;
import com.lms.quiz.quizsubmission.QuizSubmission;
import com.lms.quiz.quizsubmission.QuizSubmissionRepository;
import com.lms.user.Role;
import com.lms.user.User;
import com.lms.user.UserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LmsApplication.class)
@AutoConfigureMockMvc
public class PerformanceServiceTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceService performanceService;

    @BeforeEach
    public void setup() {
        User student = User.builder()
                .id(1)
                .name("Student One")
                .email("student1@example.com")
                .role(Role.STUDENT)
                .build();

        userRepository.save(student);

        AssignmentSubmission assignmentSubmission = AssignmentSubmission.builder()
                .id(1L)
                .student(student)
                .score(85)
                .build();

        assignmentSubmissionRepository.save(assignmentSubmission);

        QuizSubmission quizSubmission = QuizSubmission.builder()
                .quizSubmissionId(1L)
                .student(student)
                .marks(90)
                .build();

        quizSubmissionRepository.save(quizSubmission);

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setId(new AttendanceKey(1L, 1L, 1L));
        attendanceRecord.setAttended(true);

        attendanceRepository.save(attendanceRecord);
    }

    @Test
    public void generateGradesExcelReport_ShouldReturnExcelFile() throws IOException {
        byte[] report = performanceService.generateGradesExcelReport(1L);

        assertNotNull(report);
        assertTrue(report.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(report))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Grades Report", sheet.getSheetName());

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("Student ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Student Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Assignment Grade", headerRow.getCell(2).getStringCellValue());
            assertEquals("Quiz Grade", headerRow.getCell(3).getStringCellValue());
            assertEquals("Total Grade", headerRow.getCell(4).getStringCellValue());
        }
    }

    @Test
    public void generateAttendanceExcelReport_ShouldReturnExcelFile() throws IOException {
        byte[] report = performanceService.generateAttendanceExcelReport(1L);

        assertNotNull(report);
        assertTrue(report.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(report))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Attendance Report", sheet.getSheetName());

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("Student Name", headerRow.getCell(0).getStringCellValue());
            assertEquals("Lesson 1", headerRow.getCell(1).getStringCellValue());
            assertEquals("Total (%)", headerRow.getCell(2).getStringCellValue());
        }
    }
}
