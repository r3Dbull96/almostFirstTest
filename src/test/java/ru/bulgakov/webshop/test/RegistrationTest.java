package ru.bulgakov.webshop.test;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import ru.bulgakov.webshop.TestBase;
import ru.bulgakov.webshop.pages.WsWelcomePage;

import static com.codeborne.selenide.Selenide.*;
import static ru.bulgakov.webshop.config.Config.WEB_SHOP_URL;

public class RegistrationTest extends TestBase {
    private static final Faker faker = new Faker();

    @Test
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
}
