package com.lms.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendLessonRequest {
    private String otp;
}
