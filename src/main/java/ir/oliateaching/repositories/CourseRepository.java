package ir.oliateaching.repositories;

import ir.oliateaching.domains.Course;
import ir.oliateaching.domains.Student;
import ir.oliateaching.domains.Teacher;
import ir.oliateaching.enums.CourseStatus;
import ir.oliateaching.repositories.base.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByTitle(String title);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByTeacher(Long teacherId);
    List<Course> findByStudent(Long studentId);
    List<Course> findActiveCourses();
    List<Course> findUpcomingCourses();

    // Filter and search
    List<Course> searchCourses(String title, CourseStatus status, LocalDate startDate, LocalDate endDate);

    // Enrollment management
    void addStudentToCourse(Long courseId, Long studentId);
    void removeStudentFromCourse(Long courseId, Long studentId);
    List<Student> getCourseStudents(Long courseId);
    boolean isStudentEnrolled(Long courseId, Long studentId);

    // Teacher management
    void assignTeacherToCourse(Long courseId, Long teacherId);
    void removeTeacherFromCourse(Long courseId);
    Optional<Teacher> getCourseTeacher(Long courseId);
}
