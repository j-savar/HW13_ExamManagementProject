package ir.oliateaching.services;


import ir.oliateaching.domains.*;
import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import ir.oliateaching.repositories.UserRepository;
import ir.oliateaching.repositories.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;


public class AdminService {

    private final UserRepository<User> userRepository;
    private final UserService userService;
    private final CourseService courseService;

    public AdminService(EntityManager entityManager) {
        this.userRepository = new UserRepositoryImpl(entityManager);
        this.userService = new UserService(entityManager);
        this.courseService = new CourseService(entityManager);
    }

    public void initializeAdminAccount() {
        List<ir.oliateaching.domains.User> admins = userRepository.findByRole(UserRole.ADMIN);

        if (admins.isEmpty()) {
            Admin admin = new Admin(
                    "admin",
                    "admin123",
                    "System",
                    "Administrator",
                    "admin@university.edu"
            );
            admin.setPhoneNumber("09123456789");
            admin.setNationalId("1234567890");
            admin.setAdminLevel(1);
            admin.setDepartment("IT Department");
            admin.setApprovedDate(LocalDateTime.now());
            admin.setApprovedBy(1L); // Self-approved

            userRepository.save(admin);
            System.out.println("Default admin account created: admin/admin123");
        }
    }

    public List<ir.oliateaching.domains.User> getAllUsersForAdmin() {
        return userService.getAllUsers();
    }

    public List<ir.oliateaching.domains.User> getPendingRegistrations() {
        return userService.getPendingUsers();
    }

    public ir.oliateaching.domains.User approveUserRegistration(Long userId, Long adminId) {
        return userService.approveUser(userId, adminId);
    }

    public ir.oliateaching.domains.User rejectUserRegistration(Long userId, Long adminId) {
        return userService.rejectUser(userId, adminId);
    }

    public ir.oliateaching.domains.User updateUserRole(Long userId, UserRole newRole) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            user.setRole(newRole);
            return userRepository.save(user);
        }

        throw new IllegalArgumentException("User not found with id: " + userId);
    }

    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    public void assignTeacherToCourse(Long courseId, Long teacherId) {
        courseService.assignTeacherToCourse(courseId, teacherId);
    }

    public void enrollStudentInCourse(Long courseId, Long studentId) {
        var enrollmentDTO = new ir.oliateaching.dto.CourseEnrollmentDTO();
        enrollmentDTO.setCourseId(courseId);
        enrollmentDTO.setStudentId(studentId);
        courseService.enrollStudentInCourse(enrollmentDTO);
    }

    public List<Student> getCourseStudents(Long courseId) {
        return courseService.getCourseStudents(courseId);
    }

    public Teacher getCourseTeacher(Long courseId) {
        var teacherOpt = courseService.getCourseTeacher(courseId);
        return teacherOpt.orElse(null);
    }

    public AdminStatistics getStatistics() {
        AdminStatistics stats = new AdminStatistics();

        stats.totalUsers = userService.getTotalUsers();
        stats.totalStudents = userService.getTotalUsersByRole(UserRole.STUDENT);
        stats.totalTeachers = userService.getTotalUsersByRole(UserRole.TEACHER);
        stats.totalAdmins = userService.getTotalUsersByRole(UserRole.ADMIN);
        stats.pendingApprovals = userService.getPendingCount();
        stats.totalCourses = courseService.getTotalCourses();
        stats.activeCourses = courseService.getTotalCoursesByStatus(ir.oliateaching.enums.CourseStatus.ACTIVE);
        stats.plannedCourses = courseService.getTotalCoursesByStatus(ir.oliateaching.enums.CourseStatus.PLANNED);

        return stats;
    }

    public static class AdminStatistics {
        public long totalUsers;
        public long totalStudents;
        public long totalTeachers;
        public long totalAdmins;
        public long pendingApprovals;
        public long totalCourses;
        public long activeCourses;
        public long plannedCourses;

        @Override
        public String toString() {
            return "Admin Statistics:\n" +
                    "  Total Users: " + totalUsers + "\n" +
                    "  Students: " + totalStudents + "\n" +
                    "  Teachers: " + totalTeachers + "\n" +
                    "  Admins: " + totalAdmins + "\n" +
                    "  Pending Approvals: " + pendingApprovals + "\n" +
                    "  Total Courses: " + totalCourses + "\n" +
                    "  Active Courses: " + activeCourses + "\n" +
                    "  Planned Courses: " + plannedCourses;
        }
    }
}