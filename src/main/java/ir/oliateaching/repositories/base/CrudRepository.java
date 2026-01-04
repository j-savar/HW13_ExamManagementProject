package ir.oliateaching.repositories.base;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    T save(T t);
    void delete(T t);
    void deleteById(ID id);
    void deleteAll();
    Optional<T> findById(ID id);
    List<T> findAll();
    EntityManager getEntityManager();
}
