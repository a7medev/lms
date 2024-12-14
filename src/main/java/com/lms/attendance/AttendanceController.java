package com.lms.attendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private AttendanceService attendanceService;

    //generate otp
    @PostMapping("/otp")
    public ResponseEntity<String> generateOtp(@RequestParam Long courseId, @RequestParam Long lessonId) {
        String otp = otpService.generateOtp(courseId, lessonId);  
        return ResponseEntity.ok("OTP generated: " + otp);
    }

    //validate otp and mark attendance
    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(
        @RequestParam Long courseId, 
        @RequestParam Long lessonId, 
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

    //view attendance
    @GetMapping("/view")
    public ResponseEntity<List<AttendanceRecord>> viewAttendance(
        @RequestParam Long courseId,
        @RequestParam Long lessonId) {

        List<AttendanceRecord> attendanceRecords = attendanceService.getAttendance(courseId, lessonId);
        return ResponseEntity.ok(attendanceRecords);
    }
}
