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

    public WsLoginPage checkLoginPageIsOpened() {
        pageTitle.shouldHave(text("Welcome, Please Sign In!"));
        return this;
    }
    @Step("Ввести электронную почту: {email}")
    public WsLoginPage enterEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    public WsLoginPage checkRememberMe() {
        rememberMeCheckbox.click();
        return this;
    }

    @Step("Ввести пароль: {password}")
    public WsLoginPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Подтвердить авторизацию")
    public WsLoginPage submitLogin() {
        submitLoginButton.click();
        return this;
    }

    public WsLoginPage checkUserLoggedIn(String email) {
        userEmailInHeader.shouldHave(text(email));
        return this;
    }

    @Step("Проверить что появилось сообщение с ошибкой валидации пользователя")
    public WsLoginPage verifyEmailValidationErrorAppear() {
        $("span.field-validation-error").shouldBe(visible);
        return this;
    }
}
