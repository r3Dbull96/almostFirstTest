package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CartPage {
    private final SelenideElement productName = $("a.product-name");
    private final SelenideElement productQuantity = $("input.qty-input");
    private final SelenideElement totalPrice = $("span.product-subtotal");

    public String getProductName() {
        return productName.getText();
    }

    public String getProductQuantity() {
        return productQuantity.getValue();
    }

    public String getTotalPrice() {
        return totalPrice.getText();
    }
}
