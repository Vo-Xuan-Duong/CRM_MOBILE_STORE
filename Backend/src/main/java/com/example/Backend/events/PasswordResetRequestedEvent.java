package com.example.Backend.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordResetRequestedEvent extends ApplicationEvent {
    private final String email;
    private final String userName;
    private final String resetToken;

    public PasswordResetRequestedEvent(Object source, String email, String userName, String resetToken) {
        super(source);
        this.email = email;
        this.userName = userName;
        this.resetToken = resetToken;
    }
}
