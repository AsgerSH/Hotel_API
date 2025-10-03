package app.security.rest;

import app.config.HibernateConfig;
import app.dtos.UserDTO;
import app.exceptions.ApiException;
import app.security.ISecurityController;
import app.security.ISecurityDAO;
import app.security.SecurityDAO;
import app.security.User;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.TokenVerificationException;
import io.javalin.http.*;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {
    ISecurityDAO securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper objectMapper = new Utils().getObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();

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

    private static boolean userHasAllowedRole(dk.bugelhartmann.UserDTO user, Set<String> allowedRoles) {
        return user.getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }

    @Override
    public Handler login() {
        return ctx -> {
            User user = ctx.bodyAsClass(User.class);
            User verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());

            Set<String> stringRoles = verifiedUser.getRoles()
                    .stream()
                    .map(role -> role.getRolename())
                    .collect(Collectors.toSet());

            System.out.println("Succes for user: " + verifiedUser.getUsername());

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

            Set<String> stringRoles = verifiedUserRole.getRoles()
                    .stream()
                    .map(role -> role.getRolename())
                    .collect(Collectors.toSet());

            UserDTO userDTO = new UserDTO(verifiedUserRole.getUsername(), stringRoles);
            String token = createToken(userDTO);

            ObjectNode on = objectMapper.createObjectNode()
                    .put("token", token)
                    .put("username", userDTO.getUsername());

            ctx.json(on).status(201);
        };
    }

    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        return allowedRoles.isEmpty() || allowedRoles.contains("ANYONE");
    }

    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token");
        }
        return verifiedTokenUser;
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

            dk.bugelhartmann.UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);
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
            dk.bugelhartmann.UserDTO user = ctx.attribute("user");
            if (user == null) {
                throw new ForbiddenResponse("No user was added from the token");
            }
            // 3. See if any role matches
            if (!userHasAllowedRole(user, allowedRoles))
                throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        };
    }

    public String createToken(dk.bugelhartmann.UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            throw new ApiException(500, "Could not create token");
        }
    }

    private dk.bugelhartmann.UserDTO verifyToken(String token) {
        String SECRET = (System.getenv("DEPLOYED") != null)
                ? System.getenv("SECRET_KEY")
                : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new UnauthorizedResponse("Token is not valid");
            }
        } catch (ParseException | TokenVerificationException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }


}
