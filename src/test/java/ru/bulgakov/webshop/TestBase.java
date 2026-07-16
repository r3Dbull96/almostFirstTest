package ru.bulgakov.webshop;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import ru.bulgakov.webshop.config.WebDriverConfig;
import ru.bulgakov.webshop.util.AttachManager;

import java.net.URI;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.clearBrowserLocalStorage;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static ru.bulgakov.webshop.config.Config.*;

public class TestBase {
    private static final WebDriverConfig config = getWebDriverConfig();

    @BeforeAll
    static void globalSetUp() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());


        Configuration.browserSize = config.browserSize();
        Configuration.browser = config.browser();
        Configuration.timeout = config.timeout();
    }

    @BeforeEach
    void createRemoteDriver() {
        if (!"remote".equals(config.run())) {
            return;
        }

        ClientConfig clientConfig = ClientConfig.defaultConfig()
                .authenticateAs(new UsernameAndPassword(config.selenoidUser(), config.selenoidPassword()))
                .connectionTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofMinutes(2));



        WebDriver driver = RemoteWebDriver.builder()
                .address(URI.create(config.selenoidUrl()))
                .oneOf(getSelenoidChromeOptions())
                .config(clientConfig)
                .build();

        WebDriverRunner.setWebDriver(driver);
    }

    @AfterEach
    void tearDown() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }

        AttachManager.takeScreenshot();
        AttachManager.pageSource();
        AttachManager.browserConsoleLogs();

        if ("remote".equals(config.run())) {
            AttachManager.addVideo();
        }

        clearBrowserCookies();
        clearBrowserLocalStorage();
        closeWebDriver();
    }
}