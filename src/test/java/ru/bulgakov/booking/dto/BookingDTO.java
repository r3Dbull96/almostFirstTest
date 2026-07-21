package ru.bulgakov.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

    public BookingDTO(String firstname, Integer totalprice, String checkin) {
        this.firstname = firstname;
        this.totalprice = totalprice;
        this.bookingdates = new BookingDates();
        this.bookingdates.checkin = checkin;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingDates {
        private String checkin;
        private String checkout;
    }
}
