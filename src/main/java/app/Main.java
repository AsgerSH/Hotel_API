package app;

import app.config.AppSeeder;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Routes;
import app.security.rest.SecurityRoutes;

public class Main {
    private static final boolean SEED = true; // change to "true" when you want to seed

    public static void main(String[] args) {
        var emf = HibernateConfig.getEntityManagerFactory();

        if (SEED) {
            AppSeeder.seed(emf);
            System.out.println("Database seeded with roles and demo users");
        }

        ApplicationConfig.startServer(7007);
    }
}