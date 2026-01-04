package ir.oliateaching.repositories;

import ir.oliateaching.domains.User;
import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import ir.oliateaching.repositories.base.CrudRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;


public interface UserRepository <T extends User> extends CrudRepository<T,Long>{

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId);

    // Specific queries
    Optional<T> findByUsername(String username);
    Optional<T> findByEmail(String email);
    List<T> findByRole(UserRole role);
    List<T> findByStatus(UserStatus status);
    List<T> findByRoleAndStatus(UserRole role, UserStatus status);

    // Search and filter
    List<T> searchUsers(String firstName, String lastName, UserRole role, UserStatus status);
    List<T> findPendingUsers();
    List<T> findApprovedUsers();
    List<T> findUsersByRole(UserRole role);

    // Specific role queries
    Optional<T> findByStudentCode(String studentCode);
    Optional<T> findByTeacherCode(String teacherCode);

    EntityManager getEntityManager();
}
