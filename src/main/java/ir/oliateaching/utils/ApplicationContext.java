package ir.oliateaching.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Objects;


public class ApplicationContext {

    private static ApplicationContext context;
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    private ApplicationContext() {
    }


    public static synchronized ApplicationContext getInstance() {
        if (context == null) {
            context = new ApplicationContext();
        }
        return context;
    }


    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory("default");
                System.out.println("EntityManagerFactory created successfully.");
            } catch (Exception e) {
                System.err.println("Error creating EntityManagerFactory: " + e.getMessage());
                throw new RuntimeException("Error creating EntityManagerFactory", e);
            }
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
//        if (Objects.isNull(entityManager)) {
//            entityManager = getEntityManagerFactory().createEntityManager();
//        }
//        return entityManager;
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("EntityManagerFactory closed.");
        }
    }

}
