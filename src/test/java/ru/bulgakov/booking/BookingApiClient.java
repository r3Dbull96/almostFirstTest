package ru.bulgakov.booking;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.bulgakov.booking.config.BookingConfig;
import ru.bulgakov.booking.dto.AuthRequest;
import ru.bulgakov.booking.dto.AuthResponse;
import ru.bulgakov.booking.dto.BookingDTO;

import static io.restassured.RestAssured.given;
import static ru.bulgakov.booking.config.BookingApiConfig.getBookingConfig;

public class BookingApiClient {
    private static final String BOOKING_URL = "https://restful-booker.herokuapp.com";
    private static final BookingConfig config = getBookingConfig();


    public Response auth(String user, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(new AuthRequest(user, password))
                .when()
                .post(BOOKING_URL + "/auth")
                .then()
                .extract().response();
    }

    public Response createBooking(BookingDTO bookingDTO) {
        return  given()
                .contentType(ContentType.JSON)
                .body(bookingDTO)
                .post(BOOKING_URL + "/booking")
                .then()
                .extract().response();
    }

    public Response updateBooking(BookingDTO bookingDTO, Integer id) {
        return  given()
                .cookie("token", getToken())
                .contentType(ContentType.JSON)
                .body(bookingDTO)
                .pathParam("BOOKING_ID", id)
                .put(BOOKING_URL + "/booking/{BOOKING_ID}")
                .then()
                .extract().response();
    }

    private String getToken() {
        return auth(config.username(), config.password()).as(AuthResponse.class).getToken();
    }


}
