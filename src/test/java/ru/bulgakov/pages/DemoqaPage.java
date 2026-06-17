package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DemoqaPage {
    private final SelenideElement usernameInput = $("#userName");
    private final SelenideElement emailInput = $("#userEmail");
    private final SelenideElement currentAddressInput = $("#currentAddress");
    private final SelenideElement permanentAddressInput = $("#permanentAddress");
    private final SelenideElement submitButton = $("#submit");
    private final SelenideElement displayedName = $("#name");
    private final SelenideElement displayedEmail = $("#email");
    private final SelenideElement displayedCurrentAddress = $("p#currentAddress");
    private final SelenideElement displayedPermanentAddress = $("p#permanentAddress");


    public DemoqaPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public DemoqaPage setEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    public DemoqaPage setCurrentAddress(String currentAddress) {
        currentAddressInput.setValue(currentAddress);
        return this;
    }

    public DemoqaPage setPermanentAddress(String permanentAddress) {
        permanentAddressInput.setValue(permanentAddress);
        return this;
    }

    public DemoqaPage scrollToSubmit() {
        submitButton.scrollTo();
        return this;
    }

    public DemoqaPage submit() {
        submitButton.click();
        return this;
    }

    public SelenideElement getDisplayedName() {
        return displayedName;
    }

    public SelenideElement getDisplayedEmail() {
        return displayedEmail;
    }

    public SelenideElement getDisplayedCurrentAddress() {
        return displayedCurrentAddress;
    }

    public SelenideElement getDisplayedPermanentAddress() {
        return displayedPermanentAddress;
    }
}
