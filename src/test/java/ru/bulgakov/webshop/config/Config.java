package ru.bulgakov.webshop.config;

import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public static final String WEB_SHOP_URL = "https://demowebshop.tricentis.com";
    public static final String WEB_SHOP_REGISTRATION_URL = WEB_SHOP_URL + "/register";
    public static final String WEB_SHOP_LOGIN_URL = WEB_SHOP_URL + "/login";

    private static final WebDriverConfig config = ConfigFactory.create(WebDriverConfig.class, System.getProperties());

    public static WebDriverConfig getWebDriverConfig() {
        return config;
    }

    public static ChromeOptions getSelenoidChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.setCapability("browserVersion", config.browserVersion());

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("name", config.sessionName());
        selenoidOptions.put("sessionTimeout", config.sessionTimeout());
        selenoidOptions.put("env", List.of(config.timezone()));
        selenoidOptions.put("labels", Map.of("manual", "true"));
        selenoidOptions.put("enableVideo", config.enableVideo());
        selenoidOptions.put("enableVNC", config.enableVNC());

        options.setCapability("selenoid:options", selenoidOptions);
        return options;
    }
}
