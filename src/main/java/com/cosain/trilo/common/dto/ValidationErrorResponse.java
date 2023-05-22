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

    private ValidationErrorResponse(String errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }

    public void addFieldError(String errorCode, String errorMessage, String errorDetail, String field) {
        FieldErrorResponse fieldError = new FieldErrorResponse(errorCode, errorMessage, errorDetail, field);
        fieldErrors.add(fieldError);
    }

    public void addGlobalError(String errorCode, String errorMessage, String errorDetail, String objectName) {
        GlobalErrorResponse globalError = new GlobalErrorResponse(errorCode, errorMessage, errorDetail, objectName);
        globalErrors.add(globalError);
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

        private final String errorCode;
        private final String errorMessage;
        private final String errorDetail;
        private final String object;

        public GlobalErrorResponse(String errorCode, String errorMessage, String errorDetail, String object) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.errorDetail = errorDetail;
            this.object = object;
        }
    }
}
