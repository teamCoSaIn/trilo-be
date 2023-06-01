package com.cosain.trilo.common.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {

    public final List<CustomException> exceptions = new ArrayList<>();

    public CustomValidationException(List<CustomException> exceptions) {
        if (exceptions != null) {
            this.exceptions.addAll(exceptions);
        }
    }
}
