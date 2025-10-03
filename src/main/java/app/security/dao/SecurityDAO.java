package app.security.dao;

import app.config.HibernateConfig;
import app.core.exceptions.EntityNotFoundException;
import app.core.exceptions.ValidationException;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SecurityDAO implements ISecurityDAO {
    private final EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);
            if (foundUser == null) {
                throw new ValidationException("Invalid username or password");
            }
            if (!foundUser.checkPassword(password)) {
                throw new ValidationException("Invalid username or password");
            }
            return foundUser;
        }
    }


    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            User existing = em.find(User.class, username);

            if (existing != null) {
                throw new ValidationException("User already exists");
            }

            User user = new User(username, password);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }

    @Override
    public Role createRole(String rolename) {
        try (EntityManager em = emf.createEntityManager()) {
            Role role = new Role(rolename);
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
            return role;
        }
    }

    @Override
    public User addUserRole(String username, String rolename) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);
            Role foundRole = em.find(Role.class, rolename);

            if (foundUser == null) {
                throw new EntityNotFoundException("User not found");
            }
            if (foundRole == null) {
                throw new EntityNotFoundException("Role not found: " + rolename);
            }

            em.getTransaction().begin();
            foundUser.addRole(foundRole);
            em.getTransaction().commit();

            User reloaded = em.createQuery(
                            "SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username",
                            User.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();

            return reloaded;
        }
    }



    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Role userRole = new Role("USER");
            Role adminRole = new Role("ADMIN");
            em.persist(userRole);
            em.persist(adminRole);

            User user = new User("user1", "password123");
            user.addRole(userRole);
            em.persist(user);

            em.getTransaction().commit();
        }
    }
}
