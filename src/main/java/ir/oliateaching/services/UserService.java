package ir.oliateaching.services;


import ir.oliateaching.dto.UserSignUpDTO;
import ir.oliateaching.dto.UserUpdateDTO;
import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import ir.oliateaching.domains.*;
import ir.oliateaching.repositories.UserRepository;
import ir.oliateaching.repositories.UserRepositoryImpl;
import ir.oliateaching.validator.DTOValidator;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class UserService {

    private final UserRepository userRepository;

    public UserService(EntityManager entityManager) {
        this.userRepository = new UserRepositoryImpl(entityManager);
    }

    public User signUp(UserSignUpDTO dto) {
        DTOValidator.validateOrThrow(dto);

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        if (dto.getNationalId() != null && userRepository.existsByNationalId(dto.getNationalId())) {
            throw new IllegalArgumentException("National ID already exists: " + dto.getNationalId());
        }

        User user;
        switch (dto.getRole()) {
            case STUDENT:
                Student student = new Student(
                        dto.getUsername(),
                        dto.getPassword(),
                        dto.getFirstName(),
                        dto.getLastName(),
                        dto.getEmail()
                );

                if (dto.getStudentCode() != null) {
                    Optional<User> existingStudent = userRepository.findByStudentCode(dto.getStudentCode());
                    if (existingStudent.isPresent()) {
                        throw new IllegalArgumentException("Student code already exists: " + dto.getStudentCode());
                    }
                    student.setStudentCode(dto.getStudentCode());
                }

                if (dto.getFieldOfStudy() != null) {
                    student.setFieldOfStudy(dto.getFieldOfStudy());
                }

                if (dto.getEntryYear() != null) {
                    student.setEntryYear(dto.getEntryYear());
                }

                user = student;
                break;

            case TEACHER:
                Teacher teacher = new Teacher(
                        dto.getUsername(),
                        dto.getPassword(),
                        dto.getFirstName(),
                        dto.getLastName(),
                        dto.getEmail()
                );

                if (dto.getTeacherCode() != null) {
                    Optional<User> existingTeacher = userRepository.findByTeacherCode(dto.getTeacherCode());
                    if (existingTeacher.isPresent()) {
                        throw new IllegalArgumentException("Teacher code already exists: " + dto.getTeacherCode());
                    }
                    teacher.setTeacherCode(dto.getTeacherCode());
                }

                if (dto.getExpertise() != null) {
                    teacher.setExpertise(dto.getExpertise());
                }

                if (dto.getDegree() != null) {
                    teacher.setDegree(dto.getDegree());
                }

                user = teacher;
                break;

            case ADMIN:
                Admin admin = new Admin(
                        dto.getUsername(),
                        dto.getPassword(),
                        dto.getFirstName(),
                        dto.getLastName(),
                        dto.getEmail()
                );
                admin.setStatus(UserStatus.APPROVED); // Admin is auto-approved
                user = admin;
                break;

            default:
                throw new IllegalArgumentException("Invalid user role: " + dto.getRole());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getNationalId() != null) {
            user.setNationalId(dto.getNationalId());
        }

        return (User) userRepository.save(user);
    }

    public User approveUser(Long userId, Long adminId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getStatus() == UserStatus.APPROVED) {
                throw new IllegalArgumentException("User is already approved");
            }

            user.setStatus(UserStatus.APPROVED);
            user.setApprovedDate(LocalDateTime.now());
            user.setApprovedBy(adminId);

            return (User) userRepository.save(user);
        }

        throw new IllegalArgumentException("User not found with id: " + userId);
    }

    public User rejectUser(Long userId, Long adminId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(UserStatus.REJECTED);
            user.setApprovedDate(LocalDateTime.now());
            user.setApprovedBy(adminId);

            return (User) userRepository.save(user);
        }

        throw new IllegalArgumentException("User not found with id: " + userId);
    }

    public User updateUser(UserUpdateDTO dto) {
        DTOValidator.validateOrThrow(dto);

        Optional<User> userOpt = userRepository.findById(dto.getId());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Update common fields
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setRole(dto.getRole());
            user.setStatus(dto.getStatus());

            // Update role-specific fields
            if (user instanceof Student && dto.getRole() == UserRole.STUDENT) {
                Student student = (Student) user;
                student.setStudentCode(dto.getStudentCode());
                student.setFieldOfStudy(dto.getFieldOfStudy());
                student.setEntryYear(dto.getEntryYear());
            } else if (user instanceof Teacher && dto.getRole() == UserRole.TEACHER) {
                Teacher teacher = (Teacher) user;
                teacher.setTeacherCode(dto.getTeacherCode());
                teacher.setExpertise(dto.getExpertise());
                teacher.setDegree(dto.getDegree());
            }

            return (User) userRepository.save(user);
        }

        throw new IllegalArgumentException("User not found with id: " + dto.getId());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> getPendingUsers() {
        return userRepository.findPendingUsers();
    }

    public List<User> getApprovedUsers() {
        return userRepository.findApprovedUsers();
    }

    public List<User> searchUsers(String firstName, String lastName, UserRole role, UserStatus status) {
        return userRepository.searchUsers(firstName, lastName, role, status);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.isPresent() &&
                userOpt.get().getPassword().equals(password) &&
                userOpt.get().isApproved();
    }

    public long getTotalUsers() {
        return userRepository.findAll().size();
    }

    public long getTotalUsersByRole(UserRole role) {
        return userRepository.findByRole(role).size();
    }

    public long getPendingCount() {
        return userRepository.findPendingUsers().size();
    }

}
