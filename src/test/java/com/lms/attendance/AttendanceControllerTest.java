package com.lms.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.LmsApplication;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.course.lesson.Lesson;
import com.lms.course.lesson.LessonRepository;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.enrollment.EnrollmentState;
import com.lms.user.Role;
import com.lms.util.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.lms.util.FakeUserFactory.createFakeUser;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = LmsApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class AttendanceControllerTest {

    @Autowired
    private OTPService otpService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentRepository enrollmentRepository;

    @MockitoBean
    private CourseRepository courseRepository;

    @MockitoBean
    private LessonRepository lessonRepository;

    @MockitoBean
    private AttendanceRepository attendanceRepository;

    private static final long courseId = 1L;
    private static final long lessonId = 1L;
    private static final long lesson2Id = 2L;
    private static final long studentId = 20220318L;

    private Course course;
    private Lesson lesson;
    private Lesson lesson2;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        course = Course.builder()
                .title("Advanced Software Engineering")
                .description("Software Architecture and Design.")
                .courseId(courseId)
                .build();

        lesson = Lesson.builder()
                .id(lessonId)
                .title("Requirements")
                .course(course)
                .build();

        lesson2 = Lesson.builder()
                .id(lesson2Id)
                .title("Decorator Design Pattern")
                .course(course)
                .build();

        when(courseRepository.findByCourseId(courseId))
                .thenReturn(Optional.of(course));

        when(lessonRepository.findById(lessonId))
                .thenReturn(Optional.of(lesson));
        when(lessonRepository.findById(lesson2Id))
                .thenReturn(Optional.of(lesson2));

        when(enrollmentRepository.existsByCourseCourseIdAndUserIdAndEnrollmentState(courseId, studentId, EnrollmentState.ACTIVE))
                .thenReturn(true);

        when(attendanceRepository.existsById(any()))
                .thenReturn(true);
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    void testGenerateOtp() throws Exception {
        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("\\d+")));
    }

    @Test
    @WithMockCustomUser(role = Role.STUDENT, id = studentId)
    void testAttendLessonWithValidOtp() throws Exception {
        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(createFakeUser(Role.ADMIN))))
                .andExpect(status().isOk());

        String validOtp = otpService.getOtp(courseId, lessonId);

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AttendLessonRequest(validOtp))))
                .andExpect(status().isOk())
                .andExpect(content().string("Attendance marked successfully."));
    }

    @Test
    @WithMockCustomUser(role = Role.STUDENT, id = studentId)
    void testAttendLessonWithInvalidOtp() throws Exception {
        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(createFakeUser(Role.ADMIN))))
                .andExpect(status().isOk());

        String invalidOtp = "wrongOtp";

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AttendLessonRequest(invalidOtp))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid OTP."));
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    void testListAttendanceForSpecificLesson() throws Exception {
        AttendanceKey attendanceKey = new AttendanceKey(courseId, lessonId, studentId);
        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                .id(attendanceKey)
                .course(course)
                .lesson(lesson)
                .attended(true)
                .timestamp(LocalDateTime.now())
                .build();
        when(attendanceRepository.findByIdCourseIdAndIdLessonId(courseId, lessonId))
                .thenReturn(List.of(attendanceRecord));

        mockMvc.perform(get("/courses/{courseId}/attendance", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("lessonId", String.valueOf(lessonId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id.courseId").value(courseId))
                .andExpect(jsonPath("$[0].id.lessonId").value(lessonId))
                .andExpect(jsonPath("$[0].id.studentId").value(studentId))
                .andExpect(jsonPath("$[0].attended").value(true));

        verify(attendanceRepository).findByIdCourseIdAndIdLessonId(courseId, lessonId);
    }

    @Test
    @WithMockCustomUser(role = Role.ADMIN)
    void testListAttendanceForAllLessons() throws Exception {
        AttendanceKey attendanceKey1 = new AttendanceKey(courseId, lessonId, studentId);
        AttendanceKey attendanceKey2 = new AttendanceKey(courseId, lesson2Id, studentId);

        AttendanceRecord attendanceRecord1 = AttendanceRecord.builder()
                .id(attendanceKey1)
                .course(course)
                .lesson(lesson)
                .attended(true)
                .timestamp(LocalDateTime.now())
                .build();

        AttendanceRecord attendanceRecord2 = AttendanceRecord.builder()
                .id(attendanceKey2)
                .course(course)
                .lesson(lesson2)
                .attended(true)
                .timestamp(LocalDateTime.now())
                .build();

        when(attendanceRepository.findByIdCourseId(courseId))
                .thenReturn(List.of(attendanceRecord1, attendanceRecord2));

        mockMvc.perform(get("/courses/{courseId}/attendance", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id.courseId").value(courseId))
                .andExpect(jsonPath("$[0].id.lessonId").value(lessonId))
                .andExpect(jsonPath("$[0].id.studentId").value(studentId))
                .andExpect(jsonPath("$[0].attended").value(true))
                .andExpect(jsonPath("$[1].id.courseId").value(courseId))
                .andExpect(jsonPath("$[1].id.lessonId").value(lesson2Id))
                .andExpect(jsonPath("$[1].id.studentId").value(studentId))
                .andExpect(jsonPath("$[1].attended").value(true));

        verify(attendanceRepository).findByIdCourseId(courseId);
    }
}
