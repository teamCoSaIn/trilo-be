package com.cosain.trilo.config.exception;

import com.cosain.trilo.common.dto.ErrorResponse;
import com.cosain.trilo.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
    public ErrorResponse handleUnKnownException(Exception e) {
        log.error("예상치 못 한 예외!", e);

        String errorCode = "server-0001";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");
        return ErrorResponse.of(errorCode, errorMessage, errorDetail);
    }

    /**
     * 쿠키 누락 예외
     * TODO: 어떤 쿠키가 누락됐는지 API에 별도의 리스트를 추가해서 전달하면 좋을 것 같다.
     */
    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingCookieError(MissingRequestCookieException e) {
        log.info("쿠키 누락!");
        String errorCode = "request-0002";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);
        return ErrorResponse.of(errorCode, errorMessage, errorDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        log.info("인증 예외!");
        String errorCode = "auth-0001";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);
        return ErrorResponse.of(errorCode, errorMessage, errorDetail);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        log.info("인가 예외!");

        String errorCode = "auth-0002";
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");

        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);
        return ErrorResponse.of(errorCode, errorMessage, errorDetail);
    }

    /**
     * 커스텀 예외
     * 개발자에 의해 정의된 커스텀 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        String errorCode = e.getErrorCode();
        String errorMessage = getMessage(errorCode + ".message");
        String errorDetail = getMessage(errorCode + ".detail");
        HttpStatus status = e.getHttpStatus();


        log.info("[{}] errorMessage={}", errorCode, errorMessage);
        log.info("-----> errorDetail={}", errorDetail);

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(errorCode, errorMessage, errorDetail));
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
