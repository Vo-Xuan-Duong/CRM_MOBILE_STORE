package com.example.Backend.exceptions;

public class SpecException extends RuntimeException {

    public SpecException(String message) {
        super(message);
    }

    public SpecException(String message, Throwable cause) {
        super(message, cause);
    }
}
