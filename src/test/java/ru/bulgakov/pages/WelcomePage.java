package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


public class WelcomePage {
    private final SelenideElement priceLink = $$(".t-menu__list li").last(),
            goToQaModal = $("[aria-haspopup='dialog']"),
            paymentLink = $(byText("Бегу оплачивать"));


    public WelcomePage clickPrice() {
        priceLink.click();

        return this;
    }

    public WelcomePage openGoToQaModal() {
        goToQaModal.click();

        return this;
    }

    public PaymentPage choosePayment() {
        paymentLink.click();
        switchTo().window(2);

        return new PaymentPage();
    }




}
