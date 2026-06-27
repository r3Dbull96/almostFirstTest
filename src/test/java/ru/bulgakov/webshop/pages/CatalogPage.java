package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class CatalogPage {
    private final ElementsCollection listOfProducts = $$("div.product-grid div");

    public ProductPage goToProductCard(int productIndex) {
        listOfProducts.get(productIndex).click();
        return new ProductPage();
    }

}
