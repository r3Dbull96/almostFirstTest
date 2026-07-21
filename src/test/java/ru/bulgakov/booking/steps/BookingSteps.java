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
        return createBooking(randomBooking());
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
            BookingDTO booking = randomBooking();
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
                        .as("firstname должно быть равным ожидаемому")
                        .isEqualTo(expected.getFirstname()),
                () -> assertThat(actual.getLastname())
                        .as("lastname должно быть равным ожидаемому")
                        .isEqualTo(expected.getLastname()),
                () -> assertThat(actual.getTotalprice())
                        .as("totalPrice должно быть равным ожидаемому")
                        .isEqualTo(expected.getTotalprice()),
                () -> assertThat(actual.getDepositpaid())
                        .as("depositPaid должно быть равным ожидаемому")
                        .isEqualTo(expected.getDepositpaid()),
                () -> assertThat(actual.getAdditionalneeds())
                        .as("additionalNeeds должно быть равным ожидаемому")
                        .isEqualTo(expected.getAdditionalneeds()),
                () -> assertThat(actual.getBookingdates())
                        .as("bookingDates не должно быть null")
                        .isNotNull(),
                () -> assertThat(actual.getBookingdates().getCheckin())
                        .as("checkIn должно быть равным ожидаемому")
                        .isEqualTo(expected.getBookingdates().getCheckin()),
                () -> assertThat(actual.getBookingdates().getCheckout())
                        .as("checkOut должно быть равным ожидаемому")
                        .isEqualTo(expected.getBookingdates().getCheckout())
        );
    }

    @Step("Проверить соответствие всех полей в ответе для запроса с невадилными датами")
    public static void bookingWithInvalidDatesShouldBeEqual(BookingDTO expected, BookingDTO actual) {
        assertAll(
                () -> assertThat(actual.getFirstname())
                        .as("firstname должно быть равным ожидаемому")
                        .isEqualTo(expected.getFirstname()),
                () -> assertThat(actual.getLastname())
                        .as("lastname должно быть равным ожидаемому")
                        .isEqualTo(expected.getLastname()),
                () -> assertThat(actual.getTotalprice())
                        .as("totalPrice должно быть равным ожидаемому")
                        .isEqualTo(expected.getTotalprice()),
                () -> assertThat(actual.getDepositpaid())
                        .as("depositPaid должно быть равным ожидаемому")
                        .isEqualTo(expected.getDepositpaid()),
                () -> assertThat(actual.getAdditionalneeds())
                        .as("additionalNeeds должно быть равным ожидаемому")
                        .isEqualTo(expected.getAdditionalneeds()),
                () -> assertThat(actual.getBookingdates())
                        .as("bookingDates не должно быть null")
                        .isNotNull(),
                () -> assertThat(actual.getBookingdates().getCheckin())
                        .as("checkIn должно быть равным ожидаемому")
                        .isEqualTo("0NaN-aN-aN"),
                () -> assertThat(actual.getBookingdates().getCheckout())
                        .as("checkOut должно быть равным ожидаемому")
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

    public static BookingDTO randomBooking() {
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

    public static BookingDTO bookingWithoutFirstname() {
        return randomBooking().toBuilder()
                .firstname(null)
                .build();
    }

    public static BookingDTO bookingWithoutLastname() {
        return randomBooking().toBuilder()
                .lastname(null)
                .build();
    }

    public static BookingDTO bookingWithNegativeTotalPrice() {
        return randomBooking().toBuilder()
                .totalprice(-500)
                .build();
    }

    public static BookingDTO bookingWithInvalidDatePeriod() {
        return randomBooking().toBuilder()
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("2027-01-01")
                        .checkout("2026-01-01")
                        .build())
                .build();
    }

    public static BookingDTO bookingWithInvalidDates() {
        return randomBooking().toBuilder()
                .bookingdates(BookingDTO.BookingDates.builder()
                        .checkin("третье сентября")
                        .checkout("третье сентября")
                        .build())
                .build();
    }
}
