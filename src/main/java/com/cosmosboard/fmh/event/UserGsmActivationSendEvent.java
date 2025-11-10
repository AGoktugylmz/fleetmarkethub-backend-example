package com.cosmosboard.fmh.event;

import com.cosmosboard.fmh.entity.User;
import org.springframework.context.ApplicationEvent;

public class UserGsmActivationSendEvent extends ApplicationEvent {
    private final User user;

    public UserGsmActivationSendEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
