package ru.bulgakov.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class YandexSearchResultsPage {
    private final SelenideElement closeWindow = $(".DistributionButtonClose");

    public YandexSearchResultsPage closeDefaultBrowserSelectWindow() {
        closeWindow.click();

        return this;
    }

    public WelcomePage openLinkInNewTab(String webSiteName) {
        $(byText(webSiteName)).click();
        switchTo().window(1);

        return new WelcomePage();
    }




}
