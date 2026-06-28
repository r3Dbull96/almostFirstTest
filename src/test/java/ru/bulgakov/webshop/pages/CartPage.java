package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class CartPage {
    private final SelenideElement productName = $("a.product-name");
    private final SelenideElement productQuantity = $("input.qty-input");
    private final SelenideElement totalPrice = $("span.product-subtotal");

    @Step("Получить название товаров")
    public String getProductName() {
        return productName.getText();
    }

    @Step("Получить количество товаров")
    public String getProductQuantity() {
        return productQuantity.getValue();
    }

    @Step("Получить итоговую стоимость")
    public String getTotalPrice() {
        return totalPrice.getText();
    }
}
