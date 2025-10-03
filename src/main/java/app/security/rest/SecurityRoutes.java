package app.security.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import app.core.utils.Utils;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.security.RouteRole;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {
    static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private SecurityController securityController = new SecurityController();

    public EndpointGroup getSecurityRoute = () -> {
        path("/auth", () -> {
//          before(securityController::authenticate);
//          get("/", personEntityController.getAll(), Role.ANYONE);
//                get("/", personEntityController.getAll());
//                get("/resetdata", personEntityController.resetData());
//                get("/{id}", personEntityController.getById());

            post("/login", securityController.login(), Role.ANYONE);
            post("/register", securityController.register(), Role.ANYONE);
//                put("/{id}", personEntityController.update());
//                delete("/{id}", personEntityController.delete());
        });
    };

    public static EndpointGroup getSecuredRoutes() {
        return () -> {
            path("/protected", () -> {
                get("/user_demo", (ctx) -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from USER Protected")), Role.USER);
                get("/admin_demo", (ctx) -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from ADMIN Protected")), Role.ADMIN);
            });
        };
    }

    public enum Role implements RouteRole {ANYONE, USER, ADMIN}
}


