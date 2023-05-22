package com.cosain.trilo.common.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final String errorDetail;
    private final List<FieldErrorResponse> fieldErrors = new ArrayList<>();
    private final List<GlobalErrorResponse> globalErrors = new ArrayList<>();

    public static ValidationErrorResponse of(String errorCode, String errorMessage, String errorDetail) {
        return new ValidationErrorResponse(errorCode, errorMessage, errorDetail);
    }

    public void addFieldError(String errorCode, String errorMessage, String errorDetail, String field) {
        FieldErrorResponse fieldError = new FieldErrorResponse(errorCode, errorMessage, errorDetail, field);
        fieldErrors.add(fieldError);
    }


    public ValidationErrorResponse(String errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }


    @Getter
    private static class FieldErrorResponse {

        private final String errorCode;
        private final String errorMessage;
        private final String errorDetail;
        private final String field;

        public FieldErrorResponse(String errorCode, String errorMessage, String errorDetail, String field) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.errorDetail = errorDetail;
            this.field = field;
        }
    }

    @Getter
    private static class GlobalErrorResponse {

    }
}
