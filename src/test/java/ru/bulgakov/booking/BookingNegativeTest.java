package ru.bulgakov.booking;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.bulgakov.booking.dto.AuthErrorResponse;
import ru.bulgakov.booking.dto.BookingDTO;
import ru.bulgakov.booking.dto.CreateBookingResponse;
import ru.bulgakov.booking.steps.BookingSteps;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingNegativeTest extends BaseApiTest {
    /*
     * Во всех негативных тестах с бека приходят спорные ответы:
     *   при невалидной авторизации 200 код - как правило ожидается 400
     *   при бронировании без обязательных параметров 500 код - нет типизации ошибки, как правило ожидается 400
     *   успешное бронирование с отрицательной ценой, датой в невалидном формате, дата выезда раньше даты заезда - ожидается 400
     */

    private final BookingApiClient bookingApiClient = new BookingApiClient();


    @ParameterizedTest(name = "Ошибки валидации полей при авторизации")
    @Tags({@Tag("parameterized"), @Tag("api"), @Tag("negative")})
    @MethodSource("getInvalidCredentials")
    void errorAuthWithInvalidCredentialsTest(String user, String password) {
        Response response = bookingApiClient.auth(user, password);
        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.as(AuthErrorResponse.class).getReason()).isEqualTo("Bad credentials");
        assertThat(response.as(AuthErrorResponse.class).getToken()).isNull();
    }

    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Ошибка при авторизации без тела запроса")
    void errorAuthWithoutRequestBodyTest() {
        Response response = bookingApiClient.authWithoutBody();
        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.as(AuthErrorResponse.class).getReason()).isEqualTo("Bad credentials");
        assertThat(response.as(AuthErrorResponse.class).getToken()).isNull();
    }

    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Ошибка при авторизации c пустым телом запроса")
    void errorAuthWithEmptyRequestBodyTest() {
        Response response = bookingApiClient.auth(null, null);
        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.as(AuthErrorResponse.class).getReason()).isEqualTo("Bad credentials");
        assertThat(response.as(AuthErrorResponse.class).getToken()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @Tags({@Tag("parameterized"), @Tag("api"), @Tag("negative")})
    @MethodSource("getBookingRequestsWithoutReqFields")
    void errorCreateBookingWithMissingRequiredFieldsTest(String testName, BookingDTO request) {
        Response errorResponse = bookingApiClient.createBooking(request);
        assertThat(errorResponse.getStatusCode()).isEqualTo(500);

        String errorText = errorResponse.getBody().asString();
        assertThat(errorText).isEqualTo("Internal Server Error");
    }

    @ParameterizedTest(name = "{0}")
    @Tags({@Tag("parameterized"), @Tag("api"), @Tag("negative")})
    @MethodSource("getBookingRequestsWithInvalidFields")
    void createBookingWithInvalidFieldsTest(String testName, BookingDTO request) {
        Response response = bookingApiClient.createBooking(request);
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        BookingSteps.bookingShouldBeEqual(request, createBookingResponse.getBooking());
    }

    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Ошибка при создании бронирования с пустым телом запроса")
    void errorCreateBookingWithEmptyReqBodyTest() {
        Response errorResponse = bookingApiClient.createBookingWithEmptyBody();
        assertThat(errorResponse.getStatusCode()).isEqualTo(500);

        String errorText = errorResponse.getBody().asString();
        assertThat(errorText).isEqualTo("Internal Server Error");
    }


    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Успешное создание бронирования с датами в невалидном формате")
    void createBookingWithInvalidDatesTest() {
        BookingDTO request = BookingSteps.bookingWithInvalidDates();

        Response response = bookingApiClient.createBooking(request);
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);
        assertThat(createBookingResponse).isNotNull();

        BookingSteps.bookingWithInvalidDatesShouldBeEqual(request, createBookingResponse.getBooking());
    }

    static Stream<Arguments> getInvalidCredentials() {
        return Stream.of(
                Arguments.of("admin", "pswd"),
                Arguments.of("admin@gmail.com", "password123"),
                Arguments.of("admin", ""),
                Arguments.of("", "password123")
        );
    }

    static Stream<Arguments> getBookingRequestsWithoutReqFields() {
        return Stream.of(
                Arguments.of("Ошибка при создании бронирования без firstname", BookingSteps.bookingWithoutFirstname()),
                Arguments.of("Ошибка при создании бронирования без lastname", BookingSteps.bookingWithoutLastname())
        );
    }

    static Stream<Arguments> getBookingRequestsWithInvalidFields() {
        return Stream.of(
                Arguments.of("Успешное создание бронирования с отрицательной ценой", BookingSteps.bookingWithNegativeTotalPrice()),
                Arguments.of("Успешное создание бронирования с датой выезда раньше даты заезда", BookingSteps.bookingWithInvalidDatePeriod())
        );
    }
}
