package app.security;

import app.exceptions.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SecurityDAO implements ISecurityDAO {
    private final EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);
            if (foundUser.checkPassword(password)) {
                return foundUser;
            } else {
                throw new ValidationException("Invalid password");
            }
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            User existing = em.find(User.class, username);
            if (existing != null) {
                return existing;
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
            if (foundUser == null || foundRole == null) {
                throw new EntityNotFoundException("User not found");
            }

            em.getTransaction().begin();
            foundUser.addRole(foundRole);
            em.getTransaction().commit();
            return foundUser;
        }
    }
}
