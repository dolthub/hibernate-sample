package com.dolthub;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utility to grab Hibernate sessions based on branch name. Each branch is similar to its own database,
 * so we create a connection for each one. Caching of existing connections is handle in PetriDishMain.
 */
public class HibernateUtil {
    private static final String urlProp = "hibernate.connection.url";

    private static SessionFactory buildSessionFactory(String branch) {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration cfg = new Configuration().configure();

            // This is supposed to enable batch processing, but doesn't seem make a significant amount of difference.
            cfg.setProperty("hibernate.jdbc.batch_size", "100");

            if (branch == null) {
                return cfg.buildSessionFactory();
            }

            String baseUrl = cfg.getProperty(urlProp);
            String newUrl = baseUrl + "/" + branch;

            cfg.setProperty("hibernate.connection.url", newUrl);
            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
 
    public static Session getSessionForDefaultBranch() {
        return buildSessionFactory(null).openSession();
    }

    public static Session getSessionForBranch(String branch) {
        // TODO - assert good input.
        return buildSessionFactory(branch).openSession();
    }

}