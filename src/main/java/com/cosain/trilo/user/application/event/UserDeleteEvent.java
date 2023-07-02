package com.cosain.trilo.user.application.event;

import lombok.Getter;

@Getter
public class UserDeleteEvent {

    private final Long userId;
    public UserDeleteEvent(Long userId) {
        this.userId = userId;
    }

}
