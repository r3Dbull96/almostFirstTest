package ru.bulgakov.webshop.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/${run}.properties"
})
public interface WebDriverConfig extends Config {

    @Key("run")
    String run();

    @Key("browser")
    String browser();

    @Key("browserVersion")
    String browserVersion();

    @Key("browserSize")
    String browserSize();

    @Key("timeout")
    Long timeout();

    @Key("selenoidUrl")
    String selenoidUrl();

    @Key("selenoidUser")
    String selenoidUser();

    @Key("selenoidPassword")
    String selenoidPassword();

    @Key("sessionName")
    String sessionName();

    @Key("sessionTimeout")
    String sessionTimeout();

    @Key("timezone")
    String timezone();

    @Key("enableVideo")
    Boolean enableVideo();

    @Key("enableVNC")
    Boolean enableVNC();
}