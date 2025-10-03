package app.config;

import app.core.exceptions.ApiException;
import app.config.Routes;
import app.security.rest.ISecurityController;
import app.security.rest.SecurityController;
import app.security.rest.SecurityRoutes;
import app.core.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {

    private static final ObjectMapper jsonMapper = Utils.getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final Routes routes = new Routes();
    private static final ISecurityController securityController = SecurityController.getInstance();

    /**
     * Configure Javalin (called automatically in create)
     */
    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableDevLogging();              // nice for dev
        config.bundledPlugins.enableRouteOverview("/routes");  // /routes shows all endpoints
        config.http.defaultContentType = "application/json";   // default response type
        config.router.contextPath = "/api";                    // base path

        // Register routes
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());  // open endpoints
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());   // protected endpoints
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        // CORS
        app.before(ApplicationConfig::setCorsHeaders);
        app.options("/*", ApplicationConfig::setCorsHeaders);

        // Security filters
        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());

        // Exception handling
        app.exception(ApiException.class, ApplicationConfig::apiExceptionHandler);
        app.exception(Exception.class, ApplicationConfig::generalExceptionHandler);

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    // ---- Exception handling ----

    private static void generalExceptionHandler(Exception e, Context ctx) {
        logger.error("Unhandled exception", e);
        ctx.status(500)
                .json(Utils.convertToJsonMessage(ctx, "error", "Internal server error"));
    }

    private static void apiExceptionHandler(ApiException e, Context ctx) {
        logger.warn("API exception: Code={}, Message={}", e.getStatusCode(), e.getMessage());
        ctx.status(e.getStatusCode())
                .json(Utils.convertToJsonMessage(ctx, "warning", e.getMessage()));
    }

    // ---- CORS ----

    private static void setCorsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }
}
