package com.lms.attendance;

import com.lms.course.Course;
import com.lms.course.CourseRepository;
import com.lms.course.lesson.Lesson;
import com.lms.course.lesson.LessonRepository;
import com.lms.enrollment.EnrollmentRepository;
import com.lms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.lms.util.AuthUtils.hasCourseAccess;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public void markAttendance(Long courseId, Long lessonId, User student) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!hasCourseAccess(lesson.getCourse(), student, enrollmentRepository)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        AttendanceKey key = new AttendanceKey(courseId, lessonId, student.getId());
        if (!attendanceRepository.existsById(key)) {  
            AttendanceRecord record = new AttendanceRecord();
            record.setId(key);
            record.setAttended(true);
            record.setTimestamp(LocalDateTime.now());
            record.setCourse(lesson.getCourse());
            record.setLesson(lesson);
            record.setStudent(student);
            attendanceRepository.save(record);  
        }
    }

    public List<AttendanceRecord> getAttendance(Long courseId, Long lessonId, User user) {
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!hasCourseAccess(course, user, enrollmentRepository)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return attendanceRepository.findByIdCourseIdAndIdLessonId(courseId, lessonId);
    }

    public List<AttendanceRecord> getAllAttendanceForCourse(Long courseId, User user) {
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!hasCourseAccess(course, user, enrollmentRepository)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return attendanceRepository.findByIdCourseId(courseId);
    }
}
