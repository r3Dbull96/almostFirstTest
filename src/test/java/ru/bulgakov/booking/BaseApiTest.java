package ru.bulgakov.booking;

import org.junit.jupiter.api.BeforeAll;

import static ru.bulgakov.booking.util.RestAssuredSpec.setupRestAssured;

public class BaseApiTest {
    @BeforeAll
    static void setUp() {
        setupRestAssured();
    }
}
