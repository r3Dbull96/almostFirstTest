package ru.bulgakov.booking;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bulgakov.booking.dto.AuthRequest;
import ru.bulgakov.booking.dto.AuthResponse;
import ru.bulgakov.booking.dto.CreateBookingDTO;
import ru.bulgakov.booking.dto.CreateBookingResponse;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BookingTest {
    private static final String BOOKING_URL = "https://restful-booker.herokuapp.com";

    @BeforeAll
    static void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    void authTest() {
        String user = "admin";
        String password = "password123";

        AuthResponse response = given()
                .contentType(ContentType.JSON)
                .body(new AuthRequest(user, password))
                .when()
                .post(BOOKING_URL + "/auth")
                .then()
                .statusCode(200)
                .extract().as(AuthResponse.class);

        assertThat(response.getToken()).isNotNull();
    }

    @Test
    void createBookingTest() {
        CreateBookingResponse response = given()
                .contentType(ContentType.JSON)
                .body(buildBookingRequest())
                .post(BOOKING_URL + "/booking")
                .then()
                .statusCode(200)
                .extract().as(CreateBookingResponse.class);

        assertThat(response.getBookingid()).isNotNull();
        assertThat(response.getBooking().getTotalprice()).isEqualTo(1000);
        assertThat(response.getBooking().getBookingdates().getCheckin()).isEqualTo("2026-01-01");
        assertThat(response.getBooking().getDepositpaid()).isTrue();
    }

    private static CreateBookingDTO bookingRequest() {
        CreateBookingDTO booking = new CreateBookingDTO();

        booking.setFirstname("Ivan");
        booking.setLastname("Dorn");
        booking.setTotalprice(1000);
        booking.setDepositpaid(true);
        booking.setBookingdates(new CreateBookingDTO.BookingDates("2026-01-01", "2027-01-01"));
        booking.setAdditionalneeds("newspaper");

        return booking;
    }

    private static CreateBookingDTO buildBookingRequest() {
        return CreateBookingDTO.builder()
                .firstname("Ivan")
                .lastname("Dorn")
                .totalprice(1000)
                .depositpaid(true)
                .bookingdates(CreateBookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds("newspaper")
                .build();
    }


}
