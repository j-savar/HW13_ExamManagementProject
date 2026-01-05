package ir.oliateaching.utils;

import jakarta.persistence.EntityManager;

import java.util.function.Supplier;

public class JpaUtil {

    private JpaUtil() {
    }

    public static <E> E executeInTransaction(EntityManager entityManager, Supplier<E> logicSupplier) {
        boolean shouldCommit = false;
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
                shouldCommit = true;
            }
            E result = logicSupplier.get();
            if (shouldCommit && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().commit();
            }
            return result;
        } catch (Exception e) {
            if (shouldCommit && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    public static void executeInTransaction(EntityManager entityManager, Runnable logic) {
        executeInTransaction(entityManager, () -> {
            logic.run();
            return null;
        });
    }
}
