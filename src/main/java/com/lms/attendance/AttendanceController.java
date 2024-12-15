package com.lms.attendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class AttendanceController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private AttendanceService attendanceService;

    // Generate OTP 
    @PostMapping("/{courseId}/lessons/{lessonId}/generate-otp")
    public ResponseEntity<String> generateOtp(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        String otp = otpService.generateOtp(courseId, lessonId);
        return ResponseEntity.ok(otp);
    }

    // Attend a specific lesson by providing the OTP
    @PostMapping("/{courseId}/lessons/{lessonId}/attend")
    public ResponseEntity<String> attendLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam String otp,
            @RequestParam Long studentId) {
        String storedOtp = otpService.getOtp(courseId, lessonId);

        if (storedOtp != null && storedOtp.equals(otp)) {
            attendanceService.markAttendance(courseId, lessonId, studentId);
            return ResponseEntity.ok("Attendance marked successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }

    // List student's attendance for course lessons
    @GetMapping("/{courseId}/attendance")
    public ResponseEntity<List<AttendanceRecord>> listAttendance(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long lessonId) {
        List<AttendanceRecord> attendanceRecords;

        if (lessonId != null) {
            attendanceRecords = attendanceService.getAttendance(courseId, lessonId);
        } else {
            attendanceRecords = attendanceService.getAllAttendanceForCourse(courseId);
        }

        return ResponseEntity.ok(attendanceRecords);
    }
}
