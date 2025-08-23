package com.example.Backend.exceptions;

public class OrderException extends RuntimeException {

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class OrderNotFoundException extends OrderException {
        public OrderNotFoundException(Long orderId) {
            super("Order not found with ID: " + orderId);
        }
    }

    public static class InvalidOrderStatusException extends OrderException {
        public InvalidOrderStatusException(String currentStatus, String requestedStatus) {
            super("Cannot change order status from " + currentStatus + " to " + requestedStatus);
        }
    }

    public static class InsufficientStockException extends OrderException {
        public InsufficientStockException(String skuCode, int requestedQuantity, int availableQuantity) {
            super("Insufficient stock for SKU " + skuCode + ". Requested: " + requestedQuantity + ", Available: " + availableQuantity);
        }
    }
}
