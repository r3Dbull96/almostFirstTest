package ru.bulgakov.booking;

import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.bulgakov.booking.config.BookingConfig;
import ru.bulgakov.booking.dto.AuthErrorResponse;
import ru.bulgakov.booking.dto.AuthResponse;
import ru.bulgakov.booking.dto.BookingDTO;
import ru.bulgakov.booking.dto.CreateBookingResponse;
import ru.bulgakov.booking.steps.BookingSteps;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.bulgakov.booking.config.BookingApiConfig.getBookingConfig;
import static ru.bulgakov.booking.steps.BookingSteps.buildBookingRequest;

public class BookingTest extends BaseApiTest {
    private static final Faker faker = new Faker();
    private static final BookingConfig CFG = getBookingConfig();
    private final BookingApiClient bookingApiClient = new BookingApiClient();

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Авторизация пользователя")
    void authTest() {
        Response response = bookingApiClient.auth(CFG.username(), CFG.password());

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
    @Tags({@Tag("parameterized"), @Tag("api"), @Tag("negative")})
    @MethodSource("getInvalidCredentials")
    void errorAuthWithInvalidCredentialsTest(String user, String password) {
        Response response = bookingApiClient.auth(user, password);
        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.as(AuthErrorResponse.class).getReason()).isEqualTo("Bad credentials");
    }

    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Ошибка при авторизации без тела запроса")
    void errorAuthWithoutRequestBodyTest() {
        Response response = bookingApiClient.authWithoutBody();
        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.as(AuthErrorResponse.class).getReason()).isEqualTo("Bad credentials");
    }

    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Ошибка при авторизации c пустым телом запроса")
    void errorAuthWithEmptyRequestBodyTest() {
        Response response = bookingApiClient.authWithEmptyBody();
        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.as(AuthErrorResponse.class).getReason()).isEqualTo("Bad credentials");
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Создание бронирования")
    void createBookingTest() {
        BookingDTO bookingDTO = buildBookingRequest();
        Response response = bookingApiClient.createBooking(bookingDTO);
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);
        assertThat(createBookingResponse).isNotNull();

        BookingSteps.bookingShouldBeEqual(bookingDTO, createBookingResponse.getBooking());
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Получение бронирования по id")
    void getBookingTest() {
        BookingDTO bookingDTO = buildBookingRequest();
        Response createResponse = bookingApiClient.createBooking(bookingDTO);
        assertThat(createResponse.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = createResponse.as(CreateBookingResponse.class);

        Response getResponse = bookingApiClient.getBooking(createBookingResponse.getBookingid());
        assertThat(getResponse.getStatusCode()).isEqualTo(200);


        BookingSteps.bookingShouldBeEqual(createBookingResponse.getBooking(), getResponse.as(BookingDTO.class));
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Полное обновление бронирования")
    void updateBookingTest() {
        Response createResp = bookingApiClient.createBooking(buildBookingRequest());
        assertThat(createResp.getStatusCode()).isEqualTo(200);

        BookingDTO bookingDTO = buildBookingRequest();

        Response updateResponse = bookingApiClient
                .updateBooking(bookingDTO, createResp.as(CreateBookingResponse.class).getBookingid());
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);

        BookingDTO updatedBookingDTO = updateResponse.as(BookingDTO.class);

        BookingSteps.bookingShouldBeEqual(bookingDTO, updatedBookingDTO);
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Частичное обновление бронирования")
    void partialUpdateBookingTest() {
        Response createResp = bookingApiClient.createBooking(buildBookingRequest());
        assertThat(createResp.getStatusCode()).isEqualTo(200);

        BookingDTO bookingDTO = new BookingDTO(
                faker.football().players(),
                faker.number().numberBetween(10001, 12000),
                "2026-02-01");

        Response updateResponse = bookingApiClient
                .partialUpdateBooking(bookingDTO, createResp.as(CreateBookingResponse.class).getBookingid());
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);

        BookingDTO updatedBookingDTO = updateResponse.as(BookingDTO.class);

        assertAll(
                () -> assertThat(bookingDTO.getFirstname())
                        .isEqualTo(updatedBookingDTO.getFirstname()),
                () -> assertThat(bookingDTO.getTotalprice())
                        .isEqualTo(updatedBookingDTO.getTotalprice()),
                () -> assertThat(bookingDTO.getBookingdates().getCheckin())
                        .isEqualTo(updatedBookingDTO.getBookingdates().getCheckin())
        );
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Удаление бронирования")
    void deleteBookingTest() {
        Integer bookingId = bookingApiClient
                .createBooking(buildBookingRequest()).as(CreateBookingResponse.class).getBookingid();

        Response deleteResp = bookingApiClient.deleteBooking(bookingId);
        assertThat(deleteResp.getStatusCode()).isEqualTo(201);

        Response getResp = bookingApiClient.getBooking(bookingId);
        assertThat(getResp.getStatusCode()).isEqualTo(404);
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

    static Stream<Arguments> getBookingRequestsWithoutReqFields() {
        return Stream.of(
                Arguments.of("Ошибка при создании бронирования без firstname", BookingSteps.buildBookingRequestWithoutFirstname()),
                Arguments.of("Ошибка при создании бронирования без lastname", BookingSteps.buildBookingRequestWithoutLastname())
        );
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

    static Stream<Arguments> getBookingRequestsWithInvalidFields() {
        return Stream.of(
                Arguments.of("Успешное создание бронирования с отрицательной ценой", BookingSteps.buildBookingRequestWithNegativeTotalPrice()),
                Arguments.of("Успешное создание бронирования с датой выезда раньше даты заезда", BookingSteps.buildBookingRequestWithInvalidDatePeriod())
        );
    }


    @Test
    @Tags({@Tag("api"), @Tag("negative")})
    @DisplayName("Успешное создание бронирования с датами в невалидном формате")
    void createBookingWithInvalidDatesTest() {
        BookingDTO request = BookingSteps.buildBookingRequestWithInvalidDates();

        Response response = bookingApiClient.createBooking(request);
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);
        assertThat(createBookingResponse).isNotNull();

        BookingSteps.bookingWithInvalidDatesShouldBeEqual(request, createBookingResponse.getBooking());
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

    static Stream<Arguments> getInvalidCredentials() {
        return Stream.of(
                Arguments.of("admin", "pswd"),
                Arguments.of("admin@gmail.com", "password123"),
                Arguments.of("admin", ""),
                Arguments.of("", "password123")
        );
    }
}
