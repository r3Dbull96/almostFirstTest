package ru.bulgakov.booking.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import net.datafaker.Faker;
import ru.bulgakov.booking.BookingApiClient;
import ru.bulgakov.booking.dto.BookingDTO;
import ru.bulgakov.booking.dto.BookingId;
import ru.bulgakov.booking.dto.CreateBookingResponse;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class BookingSteps {
    private static final Faker faker = new Faker();
    private final BookingApiClient bookingApiClient = new BookingApiClient();

    public CreateBookingResponse createBooking() {
        return createBooking(ramdomBooking());
    }

    public CreateBookingResponse createBooking(BookingDTO booking) {
        Response response = bookingApiClient.createBooking(booking);
        step("Проверить что статус код = 200",() ->
                assertThat(response.getStatusCode()).isEqualTo(200));
        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.as(CreateBookingResponse.class);
    }

    @Step("Сгенерировать {bookingsQuantity} бронирований с фамилией {lastname}")
    public List<Integer> generateBookings(int bookingsQuantity, String lastname) {
        List<Integer> bookingIds = new ArrayList<>();
        for (int i = 0; i < bookingsQuantity; i++) {
            BookingDTO booking = ramdomBooking();
            booking.setLastname(lastname);

            Integer bookingId = createBooking(booking).getBookingid();
            bookingIds.add(bookingId);
        }
        return bookingIds;
    }

    @Step("Проверить соответствие всех полей в ответе")
    public static void bookingShouldBeEqual(BookingDTO expected, BookingDTO actual) {
        assertAll(
                () -> assertThat(actual.getFirstname())
                        .as("firstname отличается от ожидаемого")
                        .isEqualTo(expected.getFirstname()),
                () -> assertThat(actual.getLastname())
                        .as("lastname отличается от ожидаемого")
                        .isEqualTo(expected.getLastname()),
                () -> assertThat(actual.getTotalprice())
                        .as("totalPrice отличается от ожидаемого")
                        .isEqualTo(expected.getTotalprice()),
                () -> assertThat(actual.getDepositpaid())
                        .as("depositPaid отличается от ожидаемого")
                        .isEqualTo(expected.getDepositpaid()),
                () -> assertThat(actual.getAdditionalneeds())
                        .as("additionalNeeds отличается от ожидаемого")
                        .isEqualTo(expected.getAdditionalneeds()),
                () -> assertThat(actual.getBookingdates())
                        .as("bookingDates равен null")
                        .isNotNull(),
                () -> assertThat(actual.getBookingdates().getCheckin())
                        .as("checkIn отличается от ожидаемого")
                        .isEqualTo(expected.getBookingdates().getCheckin()),
                () -> assertThat(actual.getBookingdates().getCheckout())
                        .as("checkOut отличается от ожидаемого")
                        .isEqualTo(expected.getBookingdates().getCheckout())
        );
    }

    @Step("Проверить соответствие всех полей в ответе для запроса с невадилными датами")
    public static void bookingWithInvalidDatesShouldBeEqual(BookingDTO expected, BookingDTO actual) {
        assertAll(
                () -> assertThat(actual.getFirstname())
                        .as("firstname отличается от ожидаемого")
                        .isEqualTo(expected.getFirstname()),
                () -> assertThat(actual.getLastname())
                        .as("lastname отличается от ожидаемого")
                        .isEqualTo(expected.getLastname()),
                () -> assertThat(actual.getTotalprice())
                        .as("totalPrice отличается от ожидаемого")
                        .isEqualTo(expected.getTotalprice()),
                () -> assertThat(actual.getDepositpaid())
                        .as("depositPaid отличается от ожидаемого")
                        .isEqualTo(expected.getDepositpaid()),
                () -> assertThat(actual.getAdditionalneeds())
                        .as("additionalNeeds отличается от ожидаемого")
                        .isEqualTo(expected.getAdditionalneeds()),
                () -> assertThat(actual.getBookingdates())
                        .as("bookingDates равен null")
                        .isNotNull(),
                () -> assertThat(actual.getBookingdates().getCheckin())
                        .as("checkIn отличается от ожидаемого")
                        .isEqualTo("0NaN-aN-aN"),
                () -> assertThat(actual.getBookingdates().getCheckout())
                        .as("checkOut отличается от ожидаемого")
                        .isEqualTo("0NaN-aN-aN")
        );
    }

    @Step("Проверить список бронирований")
    public static void bookingListShouldBeValid(List<BookingId> bookings, List<Integer> expectedBookingIds, Integer expectedQuantity) {
        assertThat(bookings)
                .as("Количество бронирований должно совпадать с ожидаемым")
                .hasSize(expectedQuantity)
                .as("Список бронирований не должен содержать null")
                .doesNotContainNull()
                .as("Список бронирований не должен содержать дубликаты")
                .doesNotHaveDuplicates()
                .extracting(booking -> booking.bookingid())
                .as("ID бронирований дожны совпадать с ожидаемыми")
                .containsExactlyInAnyOrderElementsOf(expectedBookingIds);
    }

    public static BookingDTO ramdomBooking() {
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

    public static BookingDTO buildBookingRequestWithoutFirstname() {
        return BookingDTO.builder()
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

    public static BookingDTO buildBookingRequestWithoutLastname() {
        return BookingDTO.builder()
                .firstname(faker.name().firstName())
                .totalprice(faker.number().numberBetween(1000, 10000))
                .depositpaid(faker.bool().bool())
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds(faker.videoGame().title())
                .build();
    }

    public static BookingDTO buildBookingRequestWithNegativeTotalPrice() {
        return BookingDTO.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(-1000, -10000))
                .depositpaid(faker.bool().bool())
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2027-01-01")
                        .build())
                .additionalneeds(faker.videoGame().title())
                .build();
    }

    public static BookingDTO buildBookingRequestWithInvalidDatePeriod() {
        return BookingDTO.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(1000, 10000))
                .depositpaid(faker.bool().bool())
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2027-01-01")
                        .checkout("2026-01-01")
                        .build())
                .additionalneeds(faker.videoGame().title())
                .build();
    }

    public static BookingDTO buildBookingRequestWithInvalidDates() {
        return BookingDTO.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(1000, 10000))
                .depositpaid(faker.bool().bool())
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("третье сентября")
                        .checkout("третье сентября")
                        .build())
                .additionalneeds(faker.videoGame().title())
                .build();
    }
}
