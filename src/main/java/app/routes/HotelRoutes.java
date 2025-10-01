package app.routes;

import app.controllers.HotelController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;


public class HotelRoutes {
    HotelController controller = new HotelController();

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", controller::getAllHotels);
            get("/{id}", controller::getHotelById);
            get("/{id}/rooms", controller::getRoomsForHotel);

            // CRUD
            post("/", controller::createHotel);
            put("/{id}", controller::updateHotel);
            delete("/{id}", controller::deleteHotel);

        };
    }
}
