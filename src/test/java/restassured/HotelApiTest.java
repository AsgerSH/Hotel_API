package restassured;

import app.config.ApplicationConfig;
import app.config.Routes;
import app.security.rest.SecurityRoutes;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class HotelApiTest {

    private static Javalin app;

    @BeforeAll
    static void setup() {
        ApplicationConfig
                .getInstance()
                .initiateServer()
                .checkSecurityRoles()
                .setRoute(SecurityRoutes.getSecuredRoutes())
                .setRoute(new Routes().getRoutes())
                .setRoute(new SecurityRoutes().getSecurityRoute)
                .startServer(7007)
                .setCORS()
                .setGeneralExceptionHandling(); // or 0 for random port
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
        RestAssured.basePath = "/api/v1";
    }

//    @AfterAll
//    static void tearDown() {
//        ApplicationConfig.stopServer(app);
//    }

    @Test
    void getHotels() {
        given()
                .when().get("/hotel")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", greaterThan(0))
                .body("[0].name", notNullValue());
    }

    @Test
    void getHotelById(){
        given()
        .when().get("/hotel/1")
                .then()
                .statusCode(200)
                .log().all()
                .body("id", equalTo(1))
                .body("name", notNullValue());
    }

    @Test
    void testGetRoomsForHotel(){
        given()
                .when().get("/hotel/1/rooms")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].number", notNullValue());
    }

    @Test
    void testCreateHotel() {
        String newHotel = """
        {
          "name": "Test Hotel",
          "address": "Test Street 123",
          "rooms": 42,
          "stars": 3
        }
        """;

        given()
                .contentType("application/json")
                .body(newHotel)
                .when()
                .post("/hotel")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Hotel"))
                .body("address", equalTo("Test Street 123"));
    }

    @Test
    void testUpdateHotel() {
        String updateHotel = """
        {
          "name": "Updated Hotel",
          "address": "Ny addresse 123",
          "rooms": 42,
          "stars": 3
        }
        """;

        given()
                .contentType("application/json")
                .body(updateHotel)
                .when()
                .put("hotel/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Hotel"))
                .body("address", equalTo("Ny addresse 123"));
    }

    @Test
    void testMakeAndDeleteHotel() {
        String newHotel = """
        {
          "name": "Delete Me Hotel",
          "address": "Temp Street 1",
          "rooms": 10,
          "stars": 3
        }
        """;

        // First create a hotel
        int id = given()
                .contentType("application/json")
                .body(newHotel)
                .when()
                .post("/hotel")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Then delete it
        given()
                .when()
                .delete("/hotel/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id));
    }


}
