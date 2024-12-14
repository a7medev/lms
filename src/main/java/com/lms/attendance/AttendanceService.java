package com.lms.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void markAttendance(Long courseId, Long lessonId, Long studentId) {
        AttendanceKey key = new AttendanceKey(courseId, lessonId, studentId);
        if (!attendanceRepository.existsById(key)) {  
            AttendanceRecord record = new AttendanceRecord();
            record.setId(key);  
            record.setAttended(true);
            record.setTimestamp(LocalDateTime.now());
            attendanceRepository.save(record);  
        }
    }

    public List<AttendanceRecord> getAttendance(Long courseId, Long lessonId) {
        return attendanceRepository.findByIdCourseIdAndIdLessonId(courseId, lessonId);
    }
}
