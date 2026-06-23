package ru.bulgakov.mentor.qa;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.bulgakov.mentor.pages.DemoqaPage;
import ru.bulgakov.mentor.pages.PaymentPage;
import ru.bulgakov.mentor.pages.WelcomePage;
import ru.bulgakov.mentor.pages.YandexSearchPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlmostFirstTests {
    @Test
    @DisplayName("Проверить что цена обучения = 47000 рублей")
    @Tag("POSITIVE")
    void mentoringPriceShouldBe47000Test() {
        Configuration.pageLoadTimeout = 10000;
        Configuration.timeout = 10000;

        WelcomePage welcomePage = open("https://ya.ru/", YandexSearchPage.class)
                .search("bulgakov qa")
                .submit()
                .closeDefaultBrowserSelectWindow()
                .openLink("ivanbulgakovqa.ru");

        switchTo().window(1);

        PaymentPage paymentPage = welcomePage
                .clickPrice()
                .openGoToQaModal()
                .choosePayment();

        paymentPage.getPrice().shouldHave(text("₽ 47 000.00"));
    }

    @Test
    @DisplayName("Проверить отображение введенных в форму Text Box значений")
    @Tag("POSITIVE")
    void checkTextBoxValuesTest() {
        Configuration.pageLoadTimeout = 10000;
        Configuration.timeout = 10000;

        String name = "Иванов Иван Иванович",
                email = "test@mail.ru",
                currentAddress = "г. Москва, ул. Ленина, д. 1",
                permanentAddress = "г. Москва, ул. Малышева, д. 1/2";

        DemoqaPage demoqaPage = open("https://demoqa.com/text-box", DemoqaPage.class)
                .setUsername(name)
                .setEmail(email)
                .setCurrentAddress(currentAddress)
                .setPermanentAddress(permanentAddress)
                .scrollToSubmit()
                .submit();

        assertAll(
                () -> assertEquals("Name:" + name,
                        demoqaPage.getDisplayedName()),
                () -> assertEquals("Email:" + email,
                        demoqaPage.getDisplayedEmail()),
                () -> assertEquals("Current Address :" + currentAddress,
                        demoqaPage.getDisplayedCurrentAddress()),
                () -> assertEquals("Permananet Address :" + permanentAddress,
                        demoqaPage.getDisplayedPermanentAddress())
        );
    }
}
