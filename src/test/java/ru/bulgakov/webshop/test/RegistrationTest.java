package ru.bulgakov.webshop.test;

import io.qameta.allure.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.bulgakov.webshop.TestBase;
import ru.bulgakov.webshop.pages.WsRegistrationPage;
import ru.bulgakov.webshop.pages.WsWelcomePage;

import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bulgakov.webshop.config.Config.WEB_SHOP_URL;

@Epic("Авторизация")
@Feature("Регистрация")
@Story("Регистрация нового пользователя")
public class RegistrationTest extends TestBase {
    private static final Faker faker = new Faker();

    @Test
    @Owner("george_kiselev")
    @Tags({@Tag("positive"), @Tag("ws-site")})
    @Severity(CRITICAL)
    @Link(name = "TASK-126", url = "https://task-126.com")
    @Issue("Bag-19")
    @DisplayName("Успешная регистрация нового пользователя")
    @Description("Создаем нового пользователя со случайными данными через интерфейс")
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
    @Tags({@Tag("negative"), @Tag("ws-site")})
    @Severity(CRITICAL)
    @Owner("george_kiselev")
    @Link(name = "TASK-127", url = "https://task-127.com")
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
                () -> step("Проверить ошибку валидации email", () ->
                        assertEquals(expectedEmailError, registrationPage.getEmailError())),
                () -> step("Проверить ошибку валидации пароля", () ->
                        assertEquals(expectedPasswordError, registrationPage.getPasswordError())),
                () -> step("Проверить ошибку валидации подтверждения пароля", () ->
                        assertEquals(expectedConfirmPasswordError, registrationPage.getConfirmPasswordError()))
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