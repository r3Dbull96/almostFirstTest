package ru.bulgakov.qa;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.bulgakov.pages.YandexSearchPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class AlmostFirstTests {
    @Test
    @DisplayName("Проверить что цена обучения = 47000 рублей")
    @Tag("POSITIVE")
    void mentoringPriceShouldBe47000Test() {
        Configuration.pageLoadTimeout = 10000;
        Configuration.timeout = 10000;

        open("https://ya.ru/", YandexSearchPage.class)
                .search("bulgakov qa")
                .submit()
                .closeDefaultBrowserSelectWindow()
                .openLinkInNewTab("ivanbulgakovqa.ru")
                .clickPrice()
                .openGoToQaModal()
                .choosePayment()
                .getPrice().shouldHave(text("₽ 47 000.00"));
    }

    @Test
    void checkTextBoxValuesTest() {
        /*
         * Тест-кейс - проверить отображение введенных в форму Text Box значений
         * 1. Перейти к форме Text Box
         * 2. Заполнить все поля формы валидными значениями
         * 3. Нажать кнопку Submit
         * 4. Проверить что введенные на ш.2 значения отображаются на странице
         * */

        Configuration.pageLoadTimeout = 10000;
        Configuration.timeout = 10000;

        open("https://demoqa.com/text-box");

        $("#userName").setValue("Иванов Иван Иванович");
        $("#userEmail").setValue("test@mail.ru");
        $("#currentAddress").setValue("г. Москва, ул. Ленина, д. 1");
        $("#permanentAddress").setValue("г. Москва, ул. Малышева, д. 1/2");
        $("#submit").scrollTo().click();

        $("#name").shouldHave(text("Name:Иванов Иван Иванович"));
        $("#email").shouldHave(text("Email:test@mail.ru"));
        $("p#currentAddress").shouldHave(text("Current Address :г. Москва, ул. Ленина, д. 1"));
        $("p#permanentAddress").shouldHave(text("Permananet Address :г. Москва, ул. Малышева, д. 1/2"));
    }
}
