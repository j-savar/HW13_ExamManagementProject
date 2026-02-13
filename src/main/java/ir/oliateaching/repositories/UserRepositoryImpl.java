package ir.oliateaching.repositories;

import ir.oliateaching.domains.User;
import ir.oliateaching.domains.User_;
import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import ir.oliateaching.repositories.base.AbstractCrudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserRepositoryImpl extends AbstractCrudRepository<User, Long>
        implements UserRepository<User>{

    public UserRepositoryImpl(EntityManager entityManager) {super(entityManager);}


    @Override
    public boolean existsByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.USERNAME), username))
                .select(cb.count(root));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.EMAIL), email))
                .select(cb.count(root));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.NATIONAL_ID), nationalId))
                .select(cb.count(root));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.USERNAME), username));

        try {
            User result = entityManager.createQuery(query).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.EMAIL), email));

        try {
            User result = entityManager.createQuery(query).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByRole(UserRole role) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.ROLE), role));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get(User_.STATUS), status));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> findByRoleAndStatus(UserRole role, UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate rolePredicate = cb.equal(root.get(User_.ROLE), role);
        Predicate statusPredicate = cb.equal(root.get(User_.STATUS), status);

        query.where(cb.and(rolePredicate, statusPredicate));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> searchUsers(String firstName, String lastName, UserRole role, UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.trim().isEmpty()) {
            predicates.add(cb.like(
                    cb.lower(root.get(User_.FIRST_NAME)),
                    "%" + firstName.toLowerCase() + "%"
            ));
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            predicates.add(cb.like(
                    cb.lower(root.get(User_.LAST_NAME)),
                    "%" + lastName.toLowerCase() + "%"
            ));
        }

        if (role != null) {
            predicates.add(cb.equal(root.get(User_.ROLE), role));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get(User_.STATUS), status));
        }

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        query.orderBy(cb.asc(root.get(User_.LAST_NAME)), cb.asc(root.get(User_.FIRST_NAME)));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> findPendingUsers() {
        return findByStatus(UserStatus.PENDING);
    }

    @Override
    public List<User> findApprovedUsers() {
        return findByStatus(UserStatus.APPROVED);
    }


    @Override
    public List<User> findUsersByRole(UserRole role) {
        return findByRole(role);
    }

    @Override
    public Optional<User> findByStudentCode(String studentCode) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE TYPE(u) = Student " +
                        "AND u.studentCode = :studentCode",
                User.class
        );
        query.setParameter("studentCode", studentCode);

        try {
            User result = query.getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByTeacherCode(String teacherCode) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE TYPE(u) = Teacher " +
                        "AND u.teacherCode = :teacherCode",
                User.class
        );
        query.setParameter("teacherCode", teacherCode);

        try {
            User result = query.getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    //@SuppressWarnings("unchecked")
    protected Class<User> getEntityClass() {
        //ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return User.class; //(Class<User>) type.getActualTypeArguments()[0];
    }
}
