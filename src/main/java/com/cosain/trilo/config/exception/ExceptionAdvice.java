package com.cosain.trilo.config.exception;

import com.cosain.trilo.common.dto.BasicErrorResponse;
import com.cosain.trilo.common.dto.BusinessInputValidationErrorResponse;
import com.cosain.trilo.common.dto.ControllerInputValidationErrorResponse;
import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    /**
     * 추적하지 못 한 예외
     * -> 이 곳에서 잡힌 예외는 이후 이후, 별도의 커스텀 예외로 정의하거나 별도의 처리 방법을 도입해야한다.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BasicErrorResponse handleUnKnownException(Exception e) {
        log.error("예상치 못 한 예외!", e);

        String errorCode = "server-0001";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");
        return BasicErrorResponse.of(errorCode, errorMessage, errorDetail);
    }

    /**
     * 요청 데이터 형식 또는 데이터 타입이 올바르지 않을 때에 대한 예외 API
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("요청 데이터 형식이 올바르지 않음");
        String errorCode = "request-0001";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        return ResponseEntity
                .badRequest()
                .body(BasicErrorResponse.of(errorCode, errorMessage, errorDetail));
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();

        String errorCode = "request-0003";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        var response = ControllerInputValidationErrorResponse.of(errorCode, errorMessage, errorDetail);

        addFieldErrorsToErrorResponse(response, bindingResult.getFieldErrors());
        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /**
     * BeanValidation 후속 예외 처리 (@Valid) - 컨트롤러 검증
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();

        String errorCode = "request-0003";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        var response = ControllerInputValidationErrorResponse.of(errorCode, errorMessage, errorDetail);

        addFieldErrorsToErrorResponse(response, bindingResult.getFieldErrors());
        return ResponseEntity
                .badRequest()
                .body(response);
    }

    private void addFieldErrorsToErrorResponse(ControllerInputValidationErrorResponse response, List<FieldError> fieldErrors) {
        for (FieldError error : fieldErrors) {
            String errorCode = error.getDefaultMessage();
            String errorMessage = getMessage(errorCode + ".message");
            String errorDetail = getMessage(errorCode + ".detail");
            String field = error.getField();

            response.addError(errorCode, errorMessage, errorDetail, field);
        }
    }


    /**
     * 쿠키 누락 예외
     * TODO: 어떤 쿠키가 누락됐는지 API에 별도의 리스트를 추가해서 전달하면 좋을 것 같다.
     */
    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicErrorResponse missingCookieError(MissingRequestCookieException e) {
        log.info("쿠키 누락!");
        String errorCode = "request-0002";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);
        return BasicErrorResponse.of(errorCode, errorMessage, errorDetail);
    }

    /**
     * URL의 파라미터 변수 또는 쿼리 파라미터의 타입 에러
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("URL의 파라미터 변수 또는 쿼리 파라미터의 타입 에러");
        log.error("ex", ex);

        String errorCode = "request-0004";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);
        var response = BasicErrorResponse.of(errorCode, errorMessage, errorDetail);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 비즈니스 레이어 모델 변환 과정에서의 검증 실패 처리(비즈니스 입력 검증)
     */
    @ExceptionHandler(CustomValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessInputValidationErrorResponse handleCustomValidationException(CustomValidationException ex) {
        String errorCode = "request-0003";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        var response = BusinessInputValidationErrorResponse.of(errorCode, errorMessage, errorDetail);
        List<CustomException> exceptions = ex.getExceptions();
        addExceptionsToValidationErrorResponse(response, exceptions);
        return response;
    }

    private void addExceptionsToValidationErrorResponse(BusinessInputValidationErrorResponse response, List<CustomException> exceptions) {
        for (CustomException ex : exceptions) {
            String errorCode = ex.getErrorCode();
            String errorMessage = getMessage(errorCode + ".message");
            String errorDetail = getMessage(errorCode + ".detail");
            response.addError(errorCode, errorMessage, errorDetail);
        }
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicErrorResponse handleMultipartException(MultipartException e) {
        log.info("Multipart 요청이 아님");

        String errorCode = "request-0005";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode +".detail");

        return BasicErrorResponse.of(errorCode, errorMessage, errorDetail);
    }


    /**
     * 커스텀 예외
     * 개발자에 의해 정의된 커스텀 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BasicErrorResponse> handleCustomException(CustomException e) {
        String errorCode = e.getErrorCode();
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");
        HttpStatus status = e.getHttpStatus();


        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);

        return ResponseEntity
                .status(status)
                .body(BasicErrorResponse.of(errorCode, errorMessage, errorDetail));
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
