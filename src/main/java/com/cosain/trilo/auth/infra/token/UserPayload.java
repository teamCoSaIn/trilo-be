package com.cosain.trilo.auth.infra.token;

import lombok.Getter;

import java.util.Objects;

@Getter
public class UserPayload {

    private final Long id;

    public UserPayload(Long id){
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
