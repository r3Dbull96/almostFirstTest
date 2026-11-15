package ru.bulgakov.theCat.config;

import org.aeonbits.owner.ConfigFactory;

public class TheCatApiConfig {
    private static final TheCatConfig config = ConfigFactory.create(TheCatConfig.class, System.getProperties());

    public static TheCatConfig getTheCatConfig() {
        return config;
    }
}
