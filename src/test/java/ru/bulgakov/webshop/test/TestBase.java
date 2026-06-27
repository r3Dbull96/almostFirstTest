package ru.bulgakov.webshop.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.bulgakov.webshop.util.AttachManager;

import static com.codeborne.selenide.Selenide.*;

public class TestBase {
    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeAll
    static void before() {
        Configuration.browserSize = "1920x1080";
    }

    @AfterEach
    void after() {
        clearBrowserCookies();
        clearBrowserLocalStorage();

        AttachManager.takeScreenshot();
        AttachManager.pageSource();
        AttachManager.browserConsoleLogs();
    }

//    @BeforeEach
//    void closeDriver() {
//        closeWebDriver();
//    }
}
