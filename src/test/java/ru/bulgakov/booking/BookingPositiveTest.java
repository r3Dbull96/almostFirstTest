package ru.bulgakov.booking;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import ru.bulgakov.booking.config.BookingConfig;
import ru.bulgakov.booking.dto.*;
import ru.bulgakov.booking.steps.BookingSteps;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.bulgakov.booking.config.BookingApiConfig.getBookingConfig;
import static ru.bulgakov.booking.steps.BookingSteps.randomBooking;

public class BookingPositiveTest extends BaseApiTest {
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

    @Test
    @Tags({@Tag("api"), @Tag("positive")})
    @DisplayName("Создание бронирования")
    void createBookingTest() {
        BookingDTO booking = randomBooking();
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

        BookingDTO newBooking = randomBooking();

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
}
