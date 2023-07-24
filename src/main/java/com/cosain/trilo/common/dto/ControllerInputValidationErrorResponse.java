package com.cosain.trilo.common.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ControllerInputValidationErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final String errorDetail;
    private final List<PartErrorResponse> errors = new ArrayList<>();

    public static ControllerInputValidationErrorResponse of(String errorCode, String errorMessage, String errorDetail) {
        return new ControllerInputValidationErrorResponse(errorCode, errorMessage, errorDetail);
    }

    private ControllerInputValidationErrorResponse(String errorCode, String errorMessage, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }

    public void addError(String errorCode, String errorMessage, String errorDetail, String field) {
        var partError = new PartErrorResponse(errorCode, errorMessage, errorDetail, field);
        errors.add(partError);
    }

    @Getter
    private static class PartErrorResponse {

        private final String errorCode;
        private final String errorMessage;
        private final String errorDetail;
        private final String field;

        public PartErrorResponse(String errorCode, String errorMessage, String errorDetail, String field) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.errorDetail = errorDetail;
            this.field = field;
        }
    }
}
