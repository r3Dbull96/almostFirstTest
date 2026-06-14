package ru.bulgakov.qa;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class SearchTest {
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

        Configuration.pageLoadTimeout = 10000;
        Configuration.timeout = 10000;

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
}
