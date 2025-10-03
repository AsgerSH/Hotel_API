package app;

import app.config.ApplicationConfig;
import app.config.Routes;
import app.security.rest.SecurityRoutes;

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