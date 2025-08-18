package com.example.Backend.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {
    private final String customerEmail;
    private final String customerName;
    private final String orderNumber;
    private final String newStatus;
    private final String statusMessage;

    public OrderStatusChangedEvent(Object source, String customerEmail, String customerName,
                                 String orderNumber, String newStatus, String statusMessage) {
        super(source);
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.orderNumber = orderNumber;
        this.newStatus = newStatus;
        this.statusMessage = statusMessage;
    }
}
