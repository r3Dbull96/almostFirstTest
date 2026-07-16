package ru.bulgakov.booking.config;

import org.aeonbits.owner.ConfigFactory;

public class BookingApiConfig {
    private static final BookingConfig config = ConfigFactory.create(BookingConfig.class, System.getProperties());

    public static BookingConfig getBookingConfig() {
        return config;
    }
}
