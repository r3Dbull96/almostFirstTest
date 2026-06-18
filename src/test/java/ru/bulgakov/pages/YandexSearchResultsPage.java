package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class YandexSearchResultsPage {
    private final SelenideElement closeWindow = $(".DistributionButtonClose");

    public YandexSearchResultsPage closeDefaultBrowserSelectWindow() {
        closeWindow.click();

        return this;
    }

    public WelcomePage openLink(String webSiteName) {
        $(byText(webSiteName)).click();

        return new WelcomePage();
    }




}
