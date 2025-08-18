package com.example.Backend.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final String customerEmail;
    private final String customerName;
    private final String orderNumber;
    private final String orderDetails;

    public OrderCreatedEvent(Object source, String customerEmail, String customerName,
                           String orderNumber, String orderDetails) {
        super(source);
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.orderNumber = orderNumber;
        this.orderDetails = orderDetails;
    }
}
