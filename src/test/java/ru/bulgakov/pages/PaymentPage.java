package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {
    public SelenideElement getPrice() {
        return $(".styles-module-scss-module__t92_WG__root h3");
    }
}
