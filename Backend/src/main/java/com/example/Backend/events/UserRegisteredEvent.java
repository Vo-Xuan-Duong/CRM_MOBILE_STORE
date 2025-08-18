package com.example.Backend.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final String email;
    private final String fullName;

    public UserRegisteredEvent(Object source, String email, String fullName) {
        super(source);
        this.email = email;
        this.fullName = fullName;
    }
}
