package com.example.Backend.exceptions;

public class WarrantyException extends RuntimeException {
    public WarrantyException(String message) {
        super(message);
    }

    public WarrantyException(String message, Throwable cause) {
        super(message, cause);
    }
}
