package com.cosain.trilo.common.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BusinessInputValidationErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final String errorDetail;
    private final List<PartErrorResponse> errors = new ArrayList<>();

    public static BusinessInputValidationErrorResponse of(String errorCode, String errorMessage, String errorDetail) {
        return new BusinessInputValidationErrorResponse(errorCode, errorMessage, errorDetail);
    }

    private BusinessInputValidationErrorResponse(String errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }

    public void addError(String errorCode, String errorMessage, String errorDetail) {
        var partError = new PartErrorResponse(errorCode, errorMessage, errorDetail);
        errors.add(partError);
    }

    @Getter
    private static class PartErrorResponse {

        private final String errorCode;
        private final String errorMessage;
        private final String errorDetail;

        public PartErrorResponse(String errorCode, String errorMessage, String errorDetail) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.errorDetail = errorDetail;
        }
    }
}
