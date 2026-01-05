package ir.oliateaching.services;

import ir.oliateaching.domains.User;
import ir.oliateaching.dto.CourseDTO;
import ir.oliateaching.dto.CourseEnrollmentDTO;
import ir.oliateaching.enums.CourseStatus;
import ir.oliateaching.domains.Course;
import ir.oliateaching.domains.Student;
import ir.oliateaching.domains.Teacher;
import ir.oliateaching.repositories.CourseRepository;
import ir.oliateaching.repositories.CourseRepositoryImpl;
import ir.oliateaching.repositories.UserRepository;
import ir.oliateaching.repositories.UserRepositoryImpl;
import ir.oliateaching.validator.DTOValidator;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository<User> userRepository;

    public CourseService(EntityManager entityManager) {
        this.courseRepository = new CourseRepositoryImpl(entityManager);
        this.userRepository = new UserRepositoryImpl<>(entityManager);
    }

    public Course createCourse(CourseDTO dto) {
        DTOValidator.validateOrThrow(dto);
        Optional<Course> existingCourse = courseRepository.findByCourseCode(dto.getCourseCode());

        if (existingCourse.isPresent()) {
            throw new IllegalArgumentException("Course code already exists: " + dto.getCourseCode());
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Course course = new Course(
                dto.getCourseCode(),
                dto.getTitle(),
                dto.getStartDate(),
                dto.getEndDate()
        );

        course.setDescription(dto.getDescription());
        course.setCredit(dto.getCredit());
        course.setStatus(dto.getStatus());

        if (dto.getTeacherId() != null) {
            Optional<Teacher> teacherOpt = findTeacherById(dto.getTeacherId());
            if (teacherOpt.isPresent() && teacherOpt.get().isApproved()) {
                course.setTeacher(teacherOpt.get());
            } else {
                throw new IllegalArgumentException("Teacher not found or not approved with id: " + dto.getTeacherId());
            }
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long courseId, CourseDTO dto) {
        DTOValidator.validateOrThrow(dto);
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Optional<Course> existingCourse = courseRepository.findByCourseCode(dto.getCourseCode());
            if (existingCourse.isPresent() && !existingCourse.get().getId().equals(courseId)) {
                throw new IllegalArgumentException("Course code already exists: " + dto.getCourseCode());
            }
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            course.setCourseCode(dto.getCourseCode());
            course.setTitle(dto.getTitle());
            course.setDescription(dto.getDescription());
            course.setStartDate(dto.getStartDate());
            course.setEndDate(dto.getEndDate());
            course.setCredit(dto.getCredit());
            course.setStatus(dto.getStatus());
            if (dto.getTeacherId() != null) {
                Optional<Teacher> teacherOpt = findTeacherById(dto.getTeacherId());
                if (teacherOpt.isPresent() && teacherOpt.get().isApproved()) {
                    course.setTeacher(teacherOpt.get());
                } else {
                    throw new IllegalArgumentException("Teacher not found or not approved with id: " + dto.getTeacherId());
                }
            }
            return courseRepository.save(course);
        }
        throw new IllegalArgumentException("Course not found with id: " + courseId);
    }

    public void assignTeacherToCourse(Long courseId, Long teacherId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Teacher> teacherOpt = findTeacherById(teacherId);

        if (courseOpt.isPresent() && teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            if (!teacher.isApproved()) {
                throw new IllegalArgumentException("Teacher is not approved: " + teacherId);
            }
            courseRepository.assignTeacherToCourse(courseId, teacherId);
        } else {
            throw new IllegalArgumentException("Course or Teacher not found");
        }
    }

    public void enrollStudentInCourse(CourseEnrollmentDTO dto) {
        DTOValidator.validateOrThrow(dto);

        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        Optional<Student> studentOpt = findStudentById(dto.getStudentId());

        if (courseOpt.isPresent() && studentOpt.isPresent()) {
            Course course = courseOpt.get();
            Student student = studentOpt.get();

            if (!student.isApproved()) {
                throw new IllegalArgumentException("Student is not approved: " + student.getId());
            }

            if (courseRepository.isStudentEnrolled(dto.getCourseId(), dto.getStudentId())) {
                throw new IllegalArgumentException("Student is already enrolled in this course");
            }

            courseRepository.addStudentToCourse(dto.getCourseId(), dto.getStudentId());
        } else {
            throw new IllegalArgumentException("Course or Student not found");
        }
    }

    public void removeStudentFromCourse(Long courseId, Long studentId) {
        courseRepository.removeStudentFromCourse(courseId, studentId);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findActiveCourses();
    }

    public List<Course> getUpcomingCourses() {
        return courseRepository.findUpcomingCourses();
    }

    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacher(teacherId);
    }

    public List<Course> getCoursesByStudent(Long studentId) {
        return courseRepository.findByStudent(studentId);
    }

    public List<Course> searchCourses(String title, CourseStatus status,
                                      LocalDate startDate, LocalDate endDate) {
        return courseRepository.searchCourses(title, status, startDate, endDate);
    }

    public List<Student> getCourseStudents(Long courseId) {
        return courseRepository.getCourseStudents(courseId);
    }

    public Optional<Teacher> getCourseTeacher(Long courseId) {
        return courseRepository.getCourseTeacher(courseId);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public long getTotalCourses() {
        return courseRepository.findAll().size();
    }

    public long getTotalCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status).size();
    }

    private Optional<Teacher> findTeacherById(Long teacherId) {
        Optional<ir.oliateaching.domains.User> userOpt = userRepository.findById(teacherId);
        if (userOpt.isPresent() && userOpt.get() instanceof Teacher) {
            return Optional.of((Teacher) userOpt.get());
        }
        return Optional.empty();
    }

    private Optional<Student> findStudentById(Long studentId) {
        Optional<ir.oliateaching.domains.User> userOpt = userRepository.findById(studentId);
        if (userOpt.isPresent() && userOpt.get() instanceof Student) {
            return Optional.of((Student) userOpt.get());
        }
        return Optional.empty();
    }

}
