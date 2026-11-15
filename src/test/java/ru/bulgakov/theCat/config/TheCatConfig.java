package ru.bulgakov.theCat.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/theCat.properties"
})
public interface TheCatConfig extends Config {

    String xApiKey();

    String catBaseUrl();

}
