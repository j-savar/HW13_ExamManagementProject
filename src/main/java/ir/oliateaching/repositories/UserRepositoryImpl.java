package ir.oliateaching.repositories;

import ir.oliateaching.domains.User;
import ir.oliateaching.domains.User_;
import ir.oliateaching.enums.UserRole;
import ir.oliateaching.domains.base.BaseDomain;
import ir.oliateaching.domains.base.BaseDomain_;
import ir.oliateaching.enums.UserStatus;
import ir.oliateaching.repositories.base.AbstractCrudRepository;
import ir.oliateaching.utils.ApplicationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public abstract class UserRepositoryImpl<T extends User> extends AbstractCrudRepository<T, Long>
        implements UserRepository<T>{

    public UserRepositoryImpl(EntityManager entityManager) {super(entityManager);}


    @Override
    public boolean existsByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> tRoot = query.from(getEntityClass());
        query.where(
                cb.equal(
                        tRoot.get(User_.username), username
                )
        ).select(cb.count(tRoot));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> tRoot = query.from(getEntityClass());
        query.where(
                cb.equal(
                        tRoot.get(User_.email), email
                )
        ).select(cb.count(tRoot));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> tRoot = query.from(getEntityClass());
        query.where(
                cb.equal(
                        tRoot.get(User_.nationalId), nationalId
                )
        ).select(cb.count(tRoot));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public Optional<T> findByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(User_.username), username));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResultOrNull());
    }

    @Override
    public Optional<T> findByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(User_.email), email));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResultOrNull());
    }

    @Override
    public List<T> findByRole(UserRole role) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(User_.role), role));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> findByStatus(UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(User_.status), status));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> findByRoleAndStatus(UserRole role, UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(
                cb.equal(
                        tRoot.get(User_.role), role
                )
        ).where(
                cb.equal(
                        tRoot.get(User_.status), status
                )
        );
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> searchUsers(String firstName, String lastName, UserRole role, UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(
                cb.equal(
                        tRoot.get(User_.firstName), firstName
                )
        ).where(
                cb.equal(
                        tRoot.get(User_.lastName), lastName
                )
        ).where(
                cb.equal(
                        tRoot.get(User_.role), role
                )
        ).where(
                cb.equal(
                        tRoot.get(User_.status), status
                )
        );
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> findPendingUsers() {
        return findByStatus(UserStatus.PENDING);
    }

    @Override
    public List<T> findApprovedUsers() {
        return findByStatus(UserStatus.APPROVED);
    }

    @Override
    public List<T> findUsersByRole(UserRole role) {
        return findByRole(role);
    }

    @Override
    public Optional<T> findByStudentCode(String studentCode) {
//        EntityManager em = ApplicationContext.getEntityManager();
//        try {
//            // این کوئری نیاز به JOIN با Student دارد
//            TypedQuery<User> query = em.createQuery(
//                    "SELECT u FROM User u WHERE TYPE(u) = Student AND " +
//                            "CAST(u AS Student).studentCode = :studentCode",
//                    User.class);
//            query.setParameter("studentCode", studentCode);
//
//            List<User> results = query.getResultList();
//            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
//        } finally {
//            em.close();
//        }
        return findByUsername(studentCode);
    }

    @Override
    public Optional<T> findByTeacherCode(String teacherCode) {
//        EntityManager em = ApplicationContext.getEntityManager();
//        try {
//            TypedQuery<User> query = em.createQuery(
//                    "SELECT u FROM User u WHERE TYPE(u) = Teacher AND " +
//                            "CAST(u AS Teacher).teacherCode = :teacherCode",
//                    User.class);
//            query.setParameter("teacherCode", teacherCode);
//
//            List<User> results = query.getResultList();
//            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
//        } finally {
//            em.close();
//        }
        return findByUsername(teacherCode);
    }

}
