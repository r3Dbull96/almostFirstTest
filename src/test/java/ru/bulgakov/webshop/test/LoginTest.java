package ru.bulgakov.webshop.test;

import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.bulgakov.webshop.TestBase;
import ru.bulgakov.webshop.pages.WsLoginPage;
import ru.bulgakov.webshop.pages.WsRegistrationPage;
import ru.bulgakov.webshop.pages.WsWelcomePage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static ru.bulgakov.webshop.config.Config.*;

public class LoginTest extends TestBase {
    private static final Faker faker = new Faker();
    private String email;
    private String password;


    @Nested
    public class PositiveTests {
        @BeforeEach
        void beforeEach() {
            password = faker.harryPotter().character() + faker.number().positive();
            email = faker.internet().emailAddress();

            open(WEB_SHOP_REGISTRATION_URL, WsRegistrationPage.class)
                    .register(
                            faker.name().firstName(),
                            faker.name().lastName(),
                            email,
                            password)
                    .checkUserLoggedIn(email);

            clearBrowserCookies();
            clearBrowserLocalStorage();
        }

        @Test
        @Tags({@Tag("positive"), @Tag("ws-site")})
        @Severity(CRITICAL)
        @Owner("george_kiselev")
        @Link(name = "TASK-124", url = "https://task-124.com")
        @DisplayName("Успешный логин под новым пользователем")
        void successLoginTest() {
            open(WEB_SHOP_URL, WsWelcomePage.class)
                    .openLogin()
                    .checkLoginPageIsOpened()
                    .enterEmail(email)
                    .enterPassword(password)
                    .checkRememberMe()
                    .submitLogin()
                    .checkUserLoggedIn(email);
        }
    }

    @ParameterizedTest(name = "Ошибка валидации email при логине, email = {0}")
    @Tags({@Tag("negative"), @Tag("ws-site")})
    @Owner("george_kiselev")
    @Link(name = "TASK-125", url = "https://task-125.com")
    @CsvFileSource(resources = "/email.csv")
    void emailValidationErrorWhenLoggingTest(String email) {
        password = faker.harryPotter().character() + faker.number().positive();

        open(WEB_SHOP_LOGIN_URL, WsLoginPage.class)
                .enterEmail(email)
                .enterPassword(password)
                .submitLogin()
                .verifyEmailValidationErrorAppear();
    }
}