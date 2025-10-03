package app.features.room.rest;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;


public class RoomRoutes {
    RoomController controller = new RoomController();

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", controller::getAllRooms);
            get("/{id}", controller::getRoomById);
            post("/", controller::createRoom);
            put("/{id}", controller::updateRoom);
            delete("/{id}", controller::deleteRoom);
        };
    }
}
