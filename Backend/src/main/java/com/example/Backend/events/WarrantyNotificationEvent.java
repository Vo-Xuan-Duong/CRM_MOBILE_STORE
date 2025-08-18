package com.example.Backend.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WarrantyNotificationEvent extends ApplicationEvent {
    private final String customerEmail;
    private final String customerName;
    private final String productName;
    private final String warrantyInfo;

    public WarrantyNotificationEvent(Object source, String customerEmail, String customerName,
                                   String productName, String warrantyInfo) {
        super(source);
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.productName = productName;
        this.warrantyInfo = warrantyInfo;
    }
}
