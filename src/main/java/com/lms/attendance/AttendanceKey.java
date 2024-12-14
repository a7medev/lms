package com.lms.attendance;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AttendanceKey implements Serializable {
    
    private Long courseId;
    private Long lessonId;
    private Long studentId;

    public AttendanceKey() {}

    public AttendanceKey(Long courseId, Long lessonId, Long studentId) {
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.studentId = studentId;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceKey that = (AttendanceKey) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(lessonId, that.lessonId) &&
               Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, lessonId, studentId);
    }
}
