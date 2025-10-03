package app.security.rest;

import io.javalin.http.Handler;

public interface ISecurityController {
    Handler login();        // return token
    Handler register();     // create user + return token
    Handler authenticate(); // middleware til auth
    Handler authorize();
}
