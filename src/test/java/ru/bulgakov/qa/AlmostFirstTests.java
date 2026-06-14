package ru.bulgakov.qa;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class AlmostFirstTests {
    @Test
    void mentoringPriceShouldBe47000Test() {
        /*
        * Тест-кейс - проверить что предоплата по обучению = 47 000 р.
        * 1. Открыть поисковик (Яндекс)
        * 2. Ввести данные в поисковую строку (bulgakov qa)
        * 3. Нажать кнопку поиска
        * 4. В результатах поиска найти нужный сайт и клинуть на него
        * 5. Нажать на кнопку "Стоимость"
        * 6. Нажать на кнопку "Вкатиться в QA"
        * 7. Нажать на кнопку "Бегу оплачивать"
        * 8. Проверить что к оплате 47000 р.
        * */

        Configuration.pageLoadTimeout = 3000;
        Configuration.timeout = 3000;

        open("https://ya.ru/");
        $("#text").setValue("bulgakov qa");
        $("[type=submit]").click();
        $(".DistributionButtonClose").click();
        $(byText("ivanbulgakovqa.ru")).click();

        switchTo().window(1);

        $$(".t-menu__list li").last().click();
        $x("/html/body/div[1]/div[42]/div/div/div[32]/div/a").click();
        $(byText("Бегу оплачивать")).click();

        switchTo().window(2);

        $(".styles-module-scss-module__t92_WG__root h3").shouldHave(text("₽ 47 000.00"));
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

        Configuration.pageLoadTimeout = 3000;
        Configuration.timeout = 3000;

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
