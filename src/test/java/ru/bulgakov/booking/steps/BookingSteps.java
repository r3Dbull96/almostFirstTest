package ru.bulgakov.booking.steps;

import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.bulgakov.booking.dto.BookingDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class BookingSteps {
    private static final Faker faker = new Faker();

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

    public static BookingDTO buildBookingRequest() {
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
