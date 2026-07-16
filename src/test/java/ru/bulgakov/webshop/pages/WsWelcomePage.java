package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class WsWelcomePage {
    private final SelenideElement registerButton = $("a.ico-register");
    private final SelenideElement loginHeaderLink = $("a.ico-login");
    private final ElementsCollection listOfMenuItems = $$("ul.top-menu li a");

    @Step("Открыть страницу регистрации")
    public WsRegistrationPage openRegistration() {
        registerButton.click();
        return new WsRegistrationPage();
    }

    @Step("Открыть страницу логина")
    public WsLoginPage openLogin() {
        loginHeaderLink.click();
        return new WsLoginPage();
    }

    @Step("Перейти через меню в каталог {catalogName}")
    public CatalogPage goToCatalogFromMenu(int menuItemIndex, String catalogName) {
        listOfMenuItems.get(menuItemIndex).hover();
        $(byText(catalogName)).click();
        return new CatalogPage();
    }
}
