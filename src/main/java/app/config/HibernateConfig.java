package app.config;

import app.features.hotel.entities.Hotel;
import app.features.room.entities.Room;
import app.security.entities.Role;
import app.security.entities.User;
import app.core.utils.Utils;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;

    // For production/dev
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = createEMF(false);
        }
        return emf;
    }

    // For testing with Testcontainers
    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null) {
            emfTest = createEMF(true);
        }
        return emfTest;
    }

    // Core factory builder
    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            setBaseProperties(props);

            if (forTest) {
                setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                setDevProperties(props);
            }

            configuration.setProperties(props);
            registerEntities(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            return sf.unwrap(EntityManagerFactory.class);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Failed to initialize Hibernate: " + ex);
        }
    }

    // Register all entities here
    private static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Hotel.class);
        configuration.addAnnotatedClass(Room.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
    }

    // Common Hibernate settings
    private static void setBaseProperties(Properties props) {
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");
    }

    // For deployed environments (like Heroku/Docker)
    private static void setDeployedProperties(Properties props) {
        String dbName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + dbName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        props.setProperty("hibernate.hbm2ddl.auto", "update");
    }

    // For local development (reads config.properties)
    private static void setDevProperties(Properties props) {
        String dbName = Utils.getPropertyValue("DB_NAME", "config.properties");
        String dbUser = Utils.getPropertyValue("DB_USERNAME", "config.properties");
        String dbPass = Utils.getPropertyValue("DB_PASSWORD", "config.properties");

        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + dbName);
        props.put("hibernate.connection.username", dbUser);
        props.put("hibernate.connection.password", dbPass);
        props.put("hibernate.hbm2ddl.auto", "update");
    }

    // For tests (uses Testcontainers)
    private static void setTestProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.hbm2ddl.auto", "create-drop"); // Fresh DB each test run
        props.put("hibernate.show_sql", "false");
    }
}
