package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.populators.HotelPopulator;
import app.routes.Routes;
import app.security.rest.SecurityRoutes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig
                .getInstance()
                .initiateServer()
                .checkSecurityRoles()
                .setRoute(SecurityRoutes.getSecuredRoutes())
                .setRoute(new Routes().getRoutes())
                .setRoute(new SecurityRoutes().getSecurityRoute)
                .startServer(7007)
                .setCORS()
                .setGeneralExceptionHandling();

    }
}