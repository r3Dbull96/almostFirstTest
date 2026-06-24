package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;

public class CartPage {
    private final SelenideElement productName = $("a.product-name");
    private final SelenideElement productQuantity = $("input.qty-input");
    private final SelenideElement productPrice = $("span.product-subtotal");

    public CartPage checkProductName(String expectedProductName) {
        productName.shouldHave(text(expectedProductName));
        return this;
    }

    public CartPage checkProductQuantity(String quantity) {
        productQuantity.shouldHave(value(quantity));
        return this;
    }

    public CartPage checkProductPrice(String expectedProductPrice, String quantity) {
        productPrice.shouldHave(text(
                String.valueOf(Float.parseFloat(expectedProductPrice) * Float.parseFloat(quantity))));
        return this;
    }
}
