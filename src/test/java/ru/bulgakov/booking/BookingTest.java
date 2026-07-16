package ru.bulgakov.booking;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.bulgakov.booking.dto.*;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BookingTest {
    private static final String BOOKING_URL = "https://restful-booker.herokuapp.com";
    private static final Faker faker = new Faker();
    private static final String USER = "admin", PASSWORD = "password123";

    private final BookingApiClient bookingApiClient = new BookingApiClient();

    @BeforeAll
    static void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    void authTest() {
        Response response = bookingApiClient.auth(USER, PASSWORD);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.as(AuthResponse.class).getToken()).isNotNull();
    }

    /*
     * Во всех негативных тестах с бека приходят спорные ответы:
     *   при невалидной авторизации 200 код - как правило ожидается 400
     *   при бронировании без обязательных параметров 500 код - нет типизации ошибки, как правило ожидается 400
     *   успешное бронирование с отрицательной ценой, датой в невалидном формате, дата выезда раньше даты заезда - ожидается 400
     */
    @ParameterizedTest(name = "Ошибки валидации полей при авторизации")
    @MethodSource("getInvalidCredentials")
    void errorAuthWithInvalidCredentialsTest(String user, String password) {
        AuthErrorResponse response = given()
                .contentType(ContentType.JSON)
                .body(new AuthRequest(user, password))
                .when()
                .post(BOOKING_URL + "/auth")
                .then()
                .statusCode(200)
                .extract().as(AuthErrorResponse.class);

        assertThat(response.getReason()).isEqualTo("Bad credentials");
    }

    static Stream<Arguments> getInvalidCredentials() {
        return Stream.of(
                Arguments.of("admin", "pswd"),
                Arguments.of("admin@gmail.com", "password123"),
                Arguments.of("admin", ""),
                Arguments.of("", "password123")
        );
    }

    @Test
    @DisplayName("Ошибка при авторизации без тела запроса")
    void errorAuthWithoutRequestBodyTest() {
        AuthErrorResponse response = given()
                .contentType(ContentType.JSON)
                .when()
                .post(BOOKING_URL + "/auth")
                .then()
                .statusCode(200)
                .extract().as(AuthErrorResponse.class);

        assertThat(response.getReason()).isEqualTo("Bad credentials");
    }

    @Test
    @DisplayName("Ошибка при авторизации c пустым телом запроса")
    void errorAuthWithEmptyRequestBodyTest() {
        AuthErrorResponse response = given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post(BOOKING_URL + "/auth")
                .then()
                .statusCode(200)
                .extract().as(AuthErrorResponse.class);

        assertThat(response.getReason()).isEqualTo("Bad credentials");
    }

    @Test
    void createBookingTest() {
        Response response = bookingApiClient.createBooking(buildBookingRequest());
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);
        assertThat(createBookingResponse.getBookingid()).isNotNull();
        assertThat(createBookingResponse.getBooking().getTotalprice()).isEqualTo(1000);
        assertThat(createBookingResponse.getBooking().getBookingdates().getCheckin()).isEqualTo("2026-01-01");
        assertThat(createBookingResponse.getBooking().getDepositpaid()).isTrue();
    }

    private static BookingDTO buildBookingRequest() {
        return BookingDTO.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(1000, 10000))
                .depositpaid(faker.bool().bool())
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds(faker.videoGame().title())
                .build();
    }

    @Test
    void updateBookingTest() {
        Response createResp = bookingApiClient.createBooking(buildBookingRequest());
        assertThat(createResp.getStatusCode()).isEqualTo(200);

        BookingDTO bookingDTO = buildBookingRequest();

        Response updateResponse = bookingApiClient
                .updateBooking(bookingDTO, createResp.as(CreateBookingResponse.class).getBookingid());
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);

        BookingDTO updatedBookingDTO = updateResponse.as(BookingDTO.class);

        assertThat(updatedBookingDTO.equals(bookingDTO)).isTrue();
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("getBookingRequestsWithoutReqFields")
    void errorCreateBookingWithMissingRequiredFieldsTest(String testName, BookingDTO request) {
        String responseError = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post(BOOKING_URL + "/booking")
                .then()
                .statusCode(500)
                .extract().asString();

        assertThat(responseError).isEqualTo("Internal Server Error");
    }


    static Stream<Arguments> getBookingRequestsWithoutReqFields() {
        return Stream.of(
                Arguments.of("Ошибка при создании бронирования без firstname", buildBookingRequestWithoutFirstname()),
                Arguments.of("Ошибка при создании бронирования без lastname", buildBookingRequestWithoutLastname())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getBookingRequestsWithInvalidFields")
    void createBookingWithInvalidFieldsTest(String testName, BookingDTO request) {
        CreateBookingResponse response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post(BOOKING_URL + "/booking")
                .then()
                .statusCode(200)
                .extract().as(CreateBookingResponse.class);

        assertThat(response.getBookingid()).isNotNull();
        assertThat(response.getBooking().getTotalprice()).isEqualTo(request.getTotalprice());
        assertThat(response.getBooking().getBookingdates().getCheckin()).isEqualTo(request.getBookingdates().getCheckin());
        assertThat(response.getBooking().getBookingdates().getCheckout()).isEqualTo(request.getBookingdates().getCheckout());
        assertThat(response.getBooking().getDepositpaid()).isEqualTo(request.getDepositpaid());
        assertThat(response.getBooking().getAdditionalneeds()).isEqualTo(request.getAdditionalneeds());
    }


    @Test
    @DisplayName("Успешное создание бронирования с датами в невалидном формате")
    void createBookingWithInvalidDatesTest() {
        BookingDTO request = buildBookingRequestWithInvalidDates();

        CreateBookingResponse response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .post(BOOKING_URL + "/booking")
                .then()
                .statusCode(200)
                .extract().as(CreateBookingResponse.class);

        assertThat(response.getBookingid()).isNotNull();
        assertThat(response.getBooking().getTotalprice()).isEqualTo(request.getTotalprice());
        assertThat(response.getBooking().getBookingdates().getCheckin()).isEqualTo("0NaN-aN-aN");
        assertThat(response.getBooking().getBookingdates().getCheckout()).isEqualTo("0NaN-aN-aN");
        assertThat(response.getBooking().getDepositpaid()).isEqualTo(request.getDepositpaid());
        assertThat(response.getBooking().getAdditionalneeds()).isEqualTo(request.getAdditionalneeds());
    }


    @Test
    @DisplayName("Ошибка при создании бронирования с пустым телом запроса")
    void errorCreateBookingWithEmptyReqBodyTest() {
        String responseError = given()
                .contentType(ContentType.JSON)
                .body("{}")
                .post(BOOKING_URL + "/booking")
                .then()
                .statusCode(500)
                .extract().asString();

        assertThat(responseError).isEqualTo("Internal Server Error");
    }

    static Stream<Arguments> getBookingRequestsWithInvalidFields() {
        return Stream.of(
                Arguments.of("Успешное создание бронирования с отрицательной ценой", buildBookingRequestWithNegativeTotalPrice()),
                Arguments.of("Успешное создание бронирования с датой выезда раньше даты заезда", buildBookingRequestWithInvalidDatePeriod())
        );
    }

    private static BookingDTO buildBookingRequestWithoutFirstname() {
        return BookingDTO.builder()
                .lastname("Dorn")
                .totalprice(1000)
                .depositpaid(true)
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds("newspaper")
                .build();
    }

    private static BookingDTO buildBookingRequestWithoutLastname() {
        return BookingDTO.builder()
                .firstname("Ivan")
                .totalprice(1000)
                .depositpaid(true)
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds("newspaper")
                .build();
    }


    private static BookingDTO buildBookingRequestWithNegativeTotalPrice() {
        return BookingDTO.builder()
                .firstname("Ivan")
                .lastname("Dorn")
                .totalprice(-500)
                .depositpaid(true)
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds("newspaper")
                .build();
    }

    private static BookingDTO buildBookingRequestWithInvalidDates() {
        return BookingDTO.builder()
                .firstname("Ivan")
                .lastname("Dorn")
                .totalprice(1000)
                .depositpaid(true)
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("третье сентября")
                        .checkout("третье сентября")
                        .build())
                .additionalneeds("newspaper")
                .build();
    }

    private static BookingDTO buildBookingRequestWithInvalidDatePeriod() {
        return BookingDTO.builder()
                .firstname("Ivan")
                .lastname("Dorn")
                .totalprice(1000)
                .depositpaid(true)
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2027-01-01")
                        .checkout("2026-01-01")
                        .build())
                .additionalneeds("newspaper")
                .build();
    }
}
