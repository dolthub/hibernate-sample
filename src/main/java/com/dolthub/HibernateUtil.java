package com.dolthub;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
 
public class HibernateUtil {
    private static final String urlProp = "hibernate.connection.url";

    private static SessionFactory buildSessionFactory(String branch) {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration cfg = new Configuration().configure();

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

    public static Session getSessionFactoryForBranch(String branch) {
        return buildSessionFactory(branch).openSession();
    }

}