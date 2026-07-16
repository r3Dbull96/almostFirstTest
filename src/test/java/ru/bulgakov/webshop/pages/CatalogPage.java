package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$$;

public class CatalogPage {
    private final ElementsCollection listOfProducts = $$("div.product-grid div");

    @Step("Перейти в карточку товаров с индексом {productIndex}")
    public ProductPage goToProductCard(int productIndex) {
        listOfProducts.get(productIndex).click();
        return new ProductPage();
    }

}
