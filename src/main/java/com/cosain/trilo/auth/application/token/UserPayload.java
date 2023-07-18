package com.cosain.trilo.auth.application.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode(of = {"id"})
public class UserPayload {

    private final Long id;

    public UserPayload(Long id){
        this.id = id;
    }
}
