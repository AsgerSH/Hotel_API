package app.security.rest;

import app.core.exceptions.ApiException;
import app.security.dao.ISecurityDAO;
import app.security.dao.SecurityDAO;
import app.security.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.TokenVerificationException;
import dk.bugelhartmann.UserDTO;
import app.config.HibernateConfig;
import app.core.utils.Utils;
import io.javalin.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {
    private static ISecurityDAO securityDAO;
    ObjectMapper objectMapper = new Utils().getObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();
    private static SecurityController instance;
    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);


    private SecurityController() {
    }

    public static SecurityController getInstance() { // Singleton because we don't want multiple instances of the same class
        if (instance == null) {
            instance = new SecurityController();
        }
        securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }

    private static String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null) {
            throw new UnauthorizedResponse("Authorization header is missing");
        }

        String[] parts = header.split(" ");
        if (parts.length != 2) {
            throw new UnauthorizedResponse("Authorization header is malformed");
        }
        return parts[1];
    }

    private static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        return user.getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }

    private Set<String> extractRoleNames(User user) {
        return user.getRoles().stream()
                .map(role -> role.getRolename().toUpperCase())
                .collect(Collectors.toSet());
    }

    @Override
    public Handler login() {
        return ctx -> {
            User user = ctx.bodyAsClass(User.class);
            User verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());

            Set<String> stringRoles = extractRoleNames(verifiedUser);

            logger.info("Login success for user: {}", verifiedUser.getUsername());

            UserDTO userDTO = new UserDTO(verifiedUser.getUsername(), stringRoles);
            String token = createToken(userDTO);

            ObjectNode on = objectMapper.createObjectNode()
                    .put("token", token)
                    .put("username", userDTO.getUsername());

            ctx.json(on).status(200);
        };
    }

    @Override
    public Handler register() {
        // TODO: Opret bruger og returner token
        return (Context ctx) -> {
            User user = ctx.bodyAsClass(User.class);
            User verifiedUser = securityDAO.createUser(user.getUsername(), user.getPassword());
            User verifiedUserRole = securityDAO.addUserRole(verifiedUser.getUsername(), "USER");

            Set<String> stringRoles = extractRoleNames(verifiedUser);

            UserDTO userDTO = new UserDTO(verifiedUserRole.getUsername(), stringRoles);
            String token = createToken(userDTO);

            ObjectNode on = objectMapper.createObjectNode()
                    .put("token", token)
                    .put("username", userDTO.getUsername());

            ctx.json(on).status(201);
        };
    }

    @Override
    public Handler authenticate() {
        return ctx -> {
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }

            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            if (isOpenEndpoint(allowedRoles)) return;

            UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);
            ctx.attribute("user", verifiedTokenUser);
        };
    }

    @Override
    public Handler authorize() {
        return (Context ctx) -> {
            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            // 1. Check if the endpoint is open to all (either by not having any roles or having the ANYONE role set
            if (isOpenEndpoint(allowedRoles))
                return;
            // 2. Get user and ensure it is not null
            UserDTO user = ctx.attribute("user");
            if (user == null) {
                throw new ForbiddenResponse("No user was added from the token");
            }
            // 3. See if any role matches
            if (!userHasAllowedRole(user, allowedRoles))
                throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        };
    }

// New helper method so I can avoid repeating System.getenv in verifyToken and createToken
    private String getConfigValue(String key) {
        return (System.getenv("DEPLOYED") != null)
                ? System.getenv(key)
                : Utils.getPropertyValue(key, "config.properties");
    }

    private UserDTO verifyToken(String token) {
        String SECRET = getConfigValue("SECRET");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new UnauthorizedResponse("Token is not valid");
            }
        } catch (ParseException | TokenVerificationException e) {
            logger.warn("Token verification failed: {}", e.getMessage());
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    public String createToken(UserDTO user) {
        try {
            String ISSUER = getConfigValue("ISSUER");
            String TOKEN_EXPIRE_TIME = getConfigValue("TOKEN_EXPIRE_TIME");
            String SECRET_KEY = getConfigValue("SECRET_KEY");

            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            logger.warn("Token creation failed: {}", e.getMessage());
            throw new ApiException(500, "Could not create token");
        }
    }

    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token");
        }
        return verifiedTokenUser;
    }

    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        return allowedRoles.isEmpty() || allowedRoles.contains("ANYONE");
    }

}
