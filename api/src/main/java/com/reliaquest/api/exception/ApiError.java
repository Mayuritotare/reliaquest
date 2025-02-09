package com.reliaquest.api.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    String message;
    int code;
    LocalDateTime timestamp;
}
