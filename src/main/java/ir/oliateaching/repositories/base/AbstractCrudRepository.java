package ir.oliateaching.repositories.base;

import ir.oliateaching.domains.base.BaseDomain;

import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import ir.oliateaching.domains.User;
import ir.oliateaching.utils.ApplicationContext;
import ir.oliateaching.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import ir.oliateaching.domains.base.BaseDomain_;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
public abstract class AbstractCrudRepository<T extends BaseDomain<ID>, ID extends Number>
        implements CrudRepository<T, ID>{

    protected final EntityManager entityManager;

    @Override
    public T save(T t) {
        return JpaUtil.executeInTransaction(
                this.entityManager,
                () -> {
                    if (Objects.isNull(t.getId())) {
                        entityManager.persist(t);
                    } else {
                        return entityManager.merge(t);
                    }
                    return t;
                }
        );
    }

    @Override
    public void delete(T t) {
        deleteById(t.getId());
    }

    @Override
    public void deleteById(ID id) {
        JpaUtil.executeInTransaction(
                this.entityManager,
                () -> {
//                    TODO impl deleteByIdLogic
                    return null;
                }
        );
    }

    @Override
    public void deleteAll() {
        JpaUtil.executeInTransaction(
                entityManager,
                () -> {
                    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                    CriteriaDelete<T> delete = cb.createCriteriaDelete(getEntityClass());
                    delete.from(getEntityClass());
                    entityManager.createQuery(delete).executeUpdate();
                    return null;
                }
        );
    }

    @Override
    public Optional<T> findById(ID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(BaseDomain_.ID), id));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResultOrNull());
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(getEntityClass());
        query.from(getEntityClass());
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    protected abstract Class<T> getEntityClass();
}