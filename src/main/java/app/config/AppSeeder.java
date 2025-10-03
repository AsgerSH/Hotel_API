package app.config;

import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Simple database seeder.
 * Run manually (via Main toggle) to add roles and demo users.
 */
public class AppSeeder {

    public static void seed(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Ensure USER role exists
            Role userRole = em.find(Role.class, "USER");
            if (userRole == null) {
                userRole = new Role("USER");
                em.persist(userRole);
            }

            // Ensure ADMIN role exists
            Role adminRole = em.find(Role.class, "ADMIN");
            if (adminRole == null) {
                adminRole = new Role("ADMIN");
                em.persist(adminRole);
            }

            // Add demo USER account
            User user = em.find(User.class, "user1");
            if (user == null) {
                user = new User("user1", "password123");
                user.addRole(userRole);
                em.persist(user);
            }

            // Add demo ADMIN account
            User admin = em.find(User.class, "admin1");
            if (admin == null) {
                admin = new User("admin1", "password123");
                admin.addRole(adminRole);
                em.persist(admin);
            }

            em.getTransaction().commit();
        }
    }
}
