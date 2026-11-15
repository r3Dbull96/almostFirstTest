package ru.bulgakov.booking;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTest {
    private final BookingApiClient bookingApiClient = new BookingApiClient();

    @Test
    @Tags({@Tag("api"), @Tag("positive"), @Tag("smoke")})
    @DisplayName("Проверить доступность сервиса")
    void healthCheckTest() {
        Response response = bookingApiClient.healthCheck();

        assertThat(response.getStatusCode())
                .as("Проверить что статус-код равен ожидаемому")
                .isEqualTo(201);

        assertThat(response.getBody().asString())
                .as("Проверить что тело ответа соответствует ожидаемому")
                .isEqualTo("Created");
    }
}
