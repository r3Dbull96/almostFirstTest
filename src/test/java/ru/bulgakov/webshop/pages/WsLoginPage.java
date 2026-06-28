package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class WsLoginPage {

    private final SelenideElement pageTitle = $("div.page-title");
    private final SelenideElement emailInput = $("input#Email");
    private final SelenideElement passwordInput = $("input#Password");
    private final SelenideElement rememberMeCheckbox = $("input#RememberMe");
    private final SelenideElement submitLoginButton = $("input.login-button");
    private final SelenideElement userEmailInHeader = $$("div.header-links ul li a").get(0);

    @Step("Проверить что страница логина открылась")
    public WsLoginPage checkLoginPageIsOpened() {
        pageTitle.shouldHave(text("Welcome, Please Sign In!"));
        return this;
    }

    @Step("Ввести email {email}")
    public WsLoginPage enterEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    @Step("Отметить флаг Запомнить меня")
    public WsLoginPage checkRememberMe() {
        rememberMeCheckbox.click();
        return this;
    }

    @Step("Ввести пароль {password}")
    public WsLoginPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Нажать кнопку авторизации")
    public WsLoginPage submitLogin() {
        submitLoginButton.click();
        return this;
    }

    @Step("Проверить что после авторизации отображается введеный email {email}")
    public WsLoginPage checkUserLoggedIn(String email) {
        userEmailInHeader.shouldHave(text(email));
        return this;
    }

    @Step("Проверить отображение ошибки валидации email")
    public WsLoginPage verifyEmailValidationErrorAppear() {
        $("span.field-validation-error").shouldBe(visible);
        return this;
    }
}
