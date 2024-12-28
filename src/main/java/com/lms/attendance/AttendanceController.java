package com.lms.attendance;

import java.security.Principal;
import java.util.List;

import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.lms.util.AuthUtils.principalToUser;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class AttendanceController {
    private final OTPService otpService;
    private final AttendanceService attendanceService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @PostMapping("/{courseId}/lessons/{lessonId}/generate-otp")
    public ResponseEntity<String> generateOtp(
            @PathVariable Long courseId,
            @PathVariable Long lessonId, Principal principal) {
        User user = principalToUser(principal);
        String otp = otpService.generateOtp(courseId, lessonId, user);
        return ResponseEntity.ok(otp);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/{courseId}/lessons/{lessonId}/attend")
    public ResponseEntity<String> attendLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody AttendLessonRequest request,
            Principal principal) {
        User user = principalToUser(principal);
        String storedOtp = otpService.getOtp(courseId, lessonId);

        if (storedOtp != null && storedOtp.equals(request.getOtp())) {
            attendanceService.markAttendance(courseId, lessonId, user);
            return ResponseEntity.ok("Attendance marked successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @GetMapping("/{courseId}/attendance")
    public ResponseEntity<List<AttendanceRecord>> listAttendance(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long lessonId, Principal principal) {
        User user = principalToUser(principal);
        List<AttendanceRecord> attendanceRecords;

        if (lessonId != null) {
            attendanceRecords = attendanceService.getAttendance(courseId, lessonId, user);
        } else {
            attendanceRecords = attendanceService.getAllAttendanceForCourse(courseId, user);
        }

        return ResponseEntity.ok(attendanceRecords);
    }
}
