package ru.bulgakov.booking.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/booking.properties"
})
public interface BookingConfig extends Config {

    String username();

    String password();

    String bookingUrl();

}
