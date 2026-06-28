package ru.bulgakov.webshop.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class WsRegistrationPage {

    private final SelenideElement maleGenderRadio = $("input#gender-male");
    private final SelenideElement pageTitle = $("div.page-title");
    private final SelenideElement firstNameInput = $("input#FirstName");
    private final SelenideElement lastNameInput = $("input#LastName");
    private final SelenideElement emailInput = $("input#Email");
    private final SelenideElement passwordInput = $("input#Password");
    private final SelenideElement confirmPasswordInput = $("input#ConfirmPassword");
    private final SelenideElement submitRegistrationButton = $("input#register-button");
    private final SelenideElement resultText = $("div.result");
    private final SelenideElement userEmailInHeader = $$("div.header-links ul li a").get(0);
    private final SelenideElement emailValidationError = $("span[for=Email]");
    private final SelenideElement passwordValidationError = $("span[for=Password]");
    private final SelenideElement confirmPasswordValidationError = $("span[for=ConfirmPassword]");

    public WsRegistrationPage register(String firstName, String lastName, String email, String password) {
        selectMaleGender()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterEmail(email)
                .enterPassword(password)
                .confirmPassword(password)
                .submitRegistration()
                .checkRegistrationCompleted();
        return this;
    }

    @Step("Проверить что открыта страница регистрации")
    public WsRegistrationPage verifyRegistrationOpened() {
        pageTitle.shouldHave(text("Register"));
        return this;
    }

    @Step("Выбрать мужской пол")
    public WsRegistrationPage selectMaleGender() {
        maleGenderRadio.click();
        return this;
    }

    @Step("Ввести имя {firstName}")
    public WsRegistrationPage enterFirstName(String firstName) {
        firstNameInput.setValue(firstName);
        return this;
    }

    @Step("Ввести фамилию {lastName}")
    public WsRegistrationPage enterLastName(String lastName) {
        lastNameInput.setValue(lastName);
        return this;
    }

    @Step("Ввести email {email}")
    public WsRegistrationPage enterEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    @Step("Ввести пароль {password}")
    public WsRegistrationPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Ввести подтверждение пароля {confirmPassword}")
    public WsRegistrationPage confirmPassword(String confirmPassword) {
        confirmPasswordInput.setValue(confirmPassword);
        return this;
    }

    @Step("Нажать кнопку Зарегистрироваться")
    public WsRegistrationPage submitRegistration() {
        submitRegistrationButton.click();
        return this;
    }

    @Step("Получить текст ошибки валидации email")
    public String getEmailError() {
        return emailValidationError.getText();
    }

    @Step("Получить текст ошибки валидации пароля")
    public String getPasswordError() {
        return passwordValidationError.getText();
    }

    @Step("Получить текст ошибки валидации подтверждения пароля")
    public String getConfirmPasswordError() {
        return confirmPasswordValidationError.getText();
    }

    @Step("Проверить что отображается сообщение об успешной регистрации")
    public WsRegistrationPage checkRegistrationCompleted() {
        resultText.shouldHave(text("Your registration completed"));
        return this;
    }

    @Step("Проверить что отображается введенный при логине  email {email}")
    public WsRegistrationPage checkUserLoggedIn(String email) {
        userEmailInHeader.shouldHave(text(email));
        return this;
    }
}