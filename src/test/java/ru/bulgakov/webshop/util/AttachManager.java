package ru.bulgakov.webshop.util;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class AttachManager {

    @Attachment(value = "Last screenshot", type = "image/png")
    public static byte[] takeScreenshot() {
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Page source (HTML)", type = "text/plain")
    public static byte[] pageSource() {
        return getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    @Attachment(value = "Browser console logs", type = "text/plain")
    public static String browserConsoleLogs() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return "WebDriver has not been started yet";
        }

        try {
            List<String> logs = Selenide.getWebDriverLogs(BROWSER);
            if (logs.isEmpty()) {
                return "No browser console logs avalible";
            }
            return String.join("\n", logs);
        } catch (Exception e) {
            return "Unable to get browser console logs: " + e.getMessage();
        }
    }
}
