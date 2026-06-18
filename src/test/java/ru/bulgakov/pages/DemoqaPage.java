package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DemoqaPage {
    private final SelenideElement usernameInput = $("#userName"),
            emailInput = $("#userEmail"),
            currentAddressInput = $("#currentAddress"),
            permanentAddressInput = $("#permanentAddress"),
            submitButton = $("#submit"),
            displayedName = $("#name"),
            displayedEmail = $("#email"),
            displayedCurrentAddress = $("p#currentAddress"),
            displayedPermanentAddress = $("p#permanentAddress");


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

    public String getDisplayedName() {
        return displayedName.getText();
    }

    public String getDisplayedEmail() {
        return displayedEmail.getText();
    }

    public String getDisplayedCurrentAddress() {
        return displayedCurrentAddress.getText();
    }

    public String getDisplayedPermanentAddress() {
        return displayedPermanentAddress.getText();
    }
}
