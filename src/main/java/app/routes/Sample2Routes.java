package app.routes;

import app.controllers.SampleController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Sample2Routes {
    SampleController controller = new SampleController();

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", controller::getAllSamples);
            get("/{id}", controller::getSampleById);
            post("/", controller::createSample);
        };
    }
}
