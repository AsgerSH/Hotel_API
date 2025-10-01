package app.config;

import app.exceptions.HotelNotFoundException;
import app.exceptions.RoomNotFoundException;
import app.populators.HotelPopulator;
import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;

import java.util.Map;

public class ApplicationConfig {
    private static Routes routes = new Routes();

    public static void configuration(JavalinConfig config){
        config.showJavalinBanner = false;
        config.bundledPlugins.enableDevLogging();
        config.staticFiles.add("/public");
        config.http.defaultContentType = "application/json";
        config.bundledPlugins.enableRouteOverview("/routes");
        config.router.contextPath = "/api/v1"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        routes = new Routes();
        var app = Javalin.create(ApplicationConfig::configuration);

        // Tilføjet så jeg populerer listerne af hoteller og værelser før serveren starter
        var emf = HibernateConfig.getEntityManagerFactory();
        try (var em = emf.createEntityManager()) {
            long count = em.createQuery("SELECT COUNT(h) FROM Hotel h", Long.class).getSingleResult();
            if (count == 0) {
                HotelPopulator.populateHotels();
            }
        }

        // Tilføjet errorhandling (PT3 i opagven)
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(400).json("Invalid hotel or room: " + e.getMessage());
        });

        app.exception(HotelNotFoundException.class, (e, ctx) -> {
            ctx.status(404).json(Map.of("error", e.getMessage()));
        });

        app.exception(RoomNotFoundException.class, (e, ctx) -> {
            ctx.status(404).json(Map.of("error", e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json("Internal Server Error: " + e.getMessage());
        });

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
