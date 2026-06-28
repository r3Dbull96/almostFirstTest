package ru.bulgakov.webshop.test;

import io.qameta.allure.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.bulgakov.webshop.TestBase;
import ru.bulgakov.webshop.pages.WsRegistrationPage;
import ru.bulgakov.webshop.pages.WsWelcomePage;

import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bulgakov.webshop.config.Config.WEB_SHOP_URL;

public class RegistrationTest extends TestBase {
    private static final Faker faker = new Faker();

    @Test
    @Tag("positive")
    @DisplayName("Регистрация пользователя")
    void registrationTest() {
        String password = faker.harryPotter().character() + faker.number().positive();
        String email = faker.internet().emailAddress();

        open(WEB_SHOP_URL, WsWelcomePage.class)
                .openRegistration()
                .verifyRegistrationOpened()
                .selectMaleGender()
                .enterFirstName(faker.name().firstName())
                .enterLastName(faker.name().lastName())
                .enterEmail(email)
                .enterPassword(password)
                .confirmPassword(password)
                .submitRegistration()
                .checkRegistrationCompleted()
                .checkUserLoggedIn(email);
    }

    @ParameterizedTest(name = "Ошибки валидации полей при регистрации")
    @Tag("negative")
    @MethodSource("getInvalidCredentialsProvider")
    void fieldsValidationErrorsDuringRegistrationTest(String email, String password, String confirmPassword) {
        WsRegistrationPage registrationPage = open(WEB_SHOP_URL, WsWelcomePage.class)
                .openRegistration()
                .verifyRegistrationOpened()
                .selectMaleGender()
                .enterFirstName(faker.name().firstName())
                .enterLastName(faker.name().lastName())
                .enterEmail(email)
                .enterPassword(password)
                .confirmPassword(confirmPassword)
                .submitRegistration();

        String expectedEmailError = "Wrong email";
        String expectedPasswordError = "The password should have at least 6 characters.";
        String expectedConfirmPasswordError = "The password and confirmation password do not match.";


        assertAll(
                () -> assertEquals(expectedEmailError, registrationPage.getEmailError()),
                () -> assertEquals(expectedPasswordError, registrationPage.getPasswordError()),
                () -> assertEquals(expectedConfirmPasswordError, registrationPage.getConfirmPasswordError())
        );
    }

    static Stream<Arguments> getInvalidCredentialsProvider() {
        return Stream.of(
                Arguments.of("test", "123$$", "456!!"),
                Arguments.of("@example.com", "pswd", "test"),
                Arguments.of("test@example", "qwe", "wasd")
        );
    }
}
