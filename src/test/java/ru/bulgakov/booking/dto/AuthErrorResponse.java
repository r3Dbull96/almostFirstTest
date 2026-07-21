package ru.bulgakov.booking.dto;

import lombok.Data;

@Data
public class AuthErrorResponse {
    private String reason;
    private String token;
}
