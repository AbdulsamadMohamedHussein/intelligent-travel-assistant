package com.example.travelbackend.exception;

/** Thrown when a request is valid JSON but breaks a business rule (bad city, bad days, not found...). */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(400, message);
    }

    public int getCode() {
        return code;
    }
}
