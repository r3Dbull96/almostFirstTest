package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class YandexSearchPage {
    private final SelenideElement searchInput = $("#text");
    private final SelenideElement submitButton = $("[type=submit]");

    public YandexSearchPage search(String query) {
        searchInput.setValue(query);

        return this;
    }

    public YandexSearchResultsPage submit() {
        submitButton.click();

        return new YandexSearchResultsPage();
    }


}
