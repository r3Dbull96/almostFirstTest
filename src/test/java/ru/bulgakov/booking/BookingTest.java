package ru.bulgakov.booking;

import io.restassured.common.mapper.TypeRef;
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
import ru.bulgakov.booking.dto.*;
import ru.bulgakov.booking.steps.BookingSteps;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.bulgakov.booking.config.BookingApiConfig.getBookingConfig;
import static ru.bulgakov.booking.steps.BookingSteps.ramdomBooking;

public class BookingTest extends BaseApiTest {
    private static final Faker faker = new Faker();
    private static final BookingConfig CFG = getBookingConfig();

    private final BookingApiClient bookingApiClient = new BookingApiClient();
    private final BookingSteps bookingSteps = new BookingSteps();


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
        BookingDTO booking = ramdomBooking();
        Response response = bookingApiClient.createBooking(booking);
        assertThat(response.getStatusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);
        assertThat(createBookingResponse).isNotNull();

        BookingSteps.bookingShouldBeEqual(booking, createBookingResponse.getBooking());
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Получение бронирования по id")
    void getBookingTest() {
        CreateBookingResponse createResponse = bookingSteps.createBooking();

        Response getResponse = bookingApiClient.getBooking(createResponse.getBookingid());
        assertThat(getResponse.getStatusCode()).isEqualTo(200);

        BookingSteps.bookingShouldBeEqual(createResponse.getBooking(), getResponse.as(BookingDTO.class));
    }

    @Test
    @DisplayName("Получение списка бронирований по фамилии")
    void getBookingsByLastName() {
        int bookingsQuantity = 5;
        String lastname = faker.name().lastName();

        List<Integer> bookingIds = bookingSteps.generateBookings(bookingsQuantity, lastname);

        Response resp = bookingApiClient.getBookingsWithFilters(Map.of("lastname", lastname));
        assertThat(resp.getStatusCode()).isEqualTo(200);

        List<BookingId> bookings = resp.as(new TypeRef<List<BookingId>>() {});
        BookingSteps.bookingListShouldBeValid(bookings, bookingIds, bookingsQuantity);
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Полное обновление бронирования")
    void updateBookingTest() {
        Integer bookingId = bookingSteps.createBooking().getBookingid();

        BookingDTO newBooking = ramdomBooking();

        Response updateResponse = bookingApiClient.updateBooking(newBooking, bookingId);
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);

        BookingDTO updatedBookingDTO = updateResponse.as(BookingDTO.class);

        BookingSteps.bookingShouldBeEqual(newBooking, updatedBookingDTO);
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Частичное обновление бронирования")
    void partialUpdateBookingTest() {
        Integer bookingId = bookingSteps.createBooking().getBookingid();

        BookingDTO newBooking = new BookingDTO(
                faker.football().players(),
                faker.number().numberBetween(10001, 12000),
                "2026-02-01");

        Response updateResponse = bookingApiClient.partialUpdateBooking(newBooking, bookingId);
        assertThat(updateResponse.getStatusCode()).isEqualTo(200);

        BookingDTO updatedBookingDTO = updateResponse.as(BookingDTO.class);

        assertAll(
                () -> assertThat(newBooking.getFirstname())
                        .isEqualTo(updatedBookingDTO.getFirstname()),
                () -> assertThat(newBooking.getTotalprice())
                        .isEqualTo(updatedBookingDTO.getTotalprice()),
                () -> assertThat(newBooking.getBookingdates().getCheckin())
                        .isEqualTo(updatedBookingDTO.getBookingdates().getCheckin())
        );
    }

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Удаление бронирования")
    void deleteBookingTest() {
        Integer bookingId = bookingSteps.createBooking().getBookingid();


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
