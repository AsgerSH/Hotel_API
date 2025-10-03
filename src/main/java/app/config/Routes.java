package app.config;

import app.features.hotel.rest.HotelRoutes;
import app.features.room.rest.RoomRoutes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

private final HotelRoutes hotelRoutes = new HotelRoutes();
private final RoomRoutes roomRoutes = new RoomRoutes();

public EndpointGroup getRoutes() {
    return () -> {
        get("/", ctx -> ctx.result("Hello World"));
        path("/hotel", hotelRoutes.getRoutes());
        path("/room", roomRoutes.getRoutes());
    };
}
}
