package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class WsWelcomePage {
    private final SelenideElement registerButton = $("a.ico-register");
    private final SelenideElement loginHeaderLink = $("a.ico-login");
    private final ElementsCollection listOfMenuItems = $$("ul.top-menu li a");

    public WsRegistrationPage openRegistration() {
        registerButton.click();
        return new WsRegistrationPage();
    }

    public WsLoginPage openLogin() {
        loginHeaderLink.click();
        return new WsLoginPage();
    }

    public CatalogPage goToCatalogFromMenu(int menuItemIndex, String catalogName) {
        listOfMenuItems.get(menuItemIndex).hover();
        $(byText(catalogName)).click();
        return new CatalogPage();
    }
}
