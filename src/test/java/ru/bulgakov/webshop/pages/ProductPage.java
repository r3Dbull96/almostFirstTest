package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProductPage {
    private final ElementsCollection processorsList = $$("dl dd ul li").get(0).$$("li input");
    private final SelenideElement quantityInput = $("input.qty-input");
    private final SelenideElement addToCartButton = $("input.add-to-cart-button");
    private final SelenideElement notificationOfAdd = $("div.bar-notification.success");
    private final SelenideElement cartCounter = $("span.cart-qty");
    private final SelenideElement cartLink = $("a.ico-cart");
    private final static SelenideElement productName = $("[itemprop=name]");
    private final static SelenideElement productPrice= $("[itemprop=price]");

    public ProductPage selectProcessor(int processorIndex) {
        processorsList.get(processorIndex).click();
        return this;
    }

    public ProductPage setQuantity(String itemQuantity) {
        quantityInput.setValue(itemQuantity);
        return this;
    }

    public ProductPage addToCart() {
        addToCartButton.click();
        return this;
    }

    public ProductPage checkDisplayNotificationOfAdd() {
        notificationOfAdd.shouldBe(visible);
        return this;
    }

    public ProductPage checkChangeCartCounter(String itemQuantity) {
        cartCounter.shouldHave(text ("(" + itemQuantity + ")"));
        return this;
    }

    public String getProductName() {
        return productName.shouldBe(visible).getText();
    }

    public String getProductPrice() {
        return productPrice.shouldBe(visible).getText();
    }

    public CartPage goToCart() {
        cartLink.click();
        return new CartPage();
    }
}
