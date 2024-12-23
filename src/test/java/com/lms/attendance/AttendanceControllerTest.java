package com.lms.attendance;

import com.lms.LmsApplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = LmsApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class AttendanceControllerTest {

    @Autowired
    private OTPService otpService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGenerateOtp() throws Exception {
        Long courseId = 1L;
        Long lessonId = 1L;

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.matchesPattern("\\d+")));
    }

    @Test
    void testAttendLessonWithValidOtp() throws Exception {
        Long courseId = 1L;
        Long lessonId = 1L;
        Long studentId = 20220318L;

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String validOtp = otpService.getOtp(courseId, lessonId);

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("otp", validOtp)
                        .param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Attendance marked successfully."));
    }

    @Test
    void testAttendLessonWithInvalidOtp() throws Exception {
        Long courseId = 1L;
        Long lessonId = 1L;
        Long studentId = 20220318L;

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String invalidOtp = "wrongOtp";

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("otp", invalidOtp)
                        .param("studentId", String.valueOf(studentId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid OTP."));
    }

    @Test
    void testListAttendanceForSpecificLesson() throws Exception {
        Long courseId = 1L;
        Long lessonId = 1L;
        Long studentId = 20220318L;

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String validOtp = otpService.getOtp(courseId, lessonId);

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("otp", validOtp)
                        .param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/courses/{courseId}/attendance", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("lessonId", String.valueOf(lessonId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id.courseId").value(courseId))
                .andExpect(jsonPath("$[0].id.lessonId").value(lessonId))
                .andExpect(jsonPath("$[0].id.studentId").value(studentId))
                .andExpect(jsonPath("$[0].attended").value(true));
    }

    @Test
    void testListAttendanceForAllLessons() throws Exception {
        Long courseId = 1L;
        Long lessonId1 = 1L;
        Long lessonId2 = 2L;
        Long studentId = 20220318L;

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String validOtp1 = otpService.getOtp(courseId, lessonId1);

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("otp", validOtp1)
                        .param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/generate-otp", courseId, lessonId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String validOtp2 = otpService.getOtp(courseId, lessonId2);

        mockMvc.perform(post("/courses/{courseId}/lessons/{lessonId}/attend", courseId, lessonId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("otp", validOtp2)
                        .param("studentId", String.valueOf(studentId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/courses/{courseId}/attendance", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}
