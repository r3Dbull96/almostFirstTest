package ru.bulgakov.webshop.test;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

public class APITests {

    @Test
    @Tags({@Tag("UI"), @Tag("positive")})
    void apiTest1() {
        System.out.println("API positive test");
    }

    @Test
    @Tags({@Tag("UI"), @Tag("negative")})
    void apiTest2() {
        System.out.println("API negative test");
    }
}
