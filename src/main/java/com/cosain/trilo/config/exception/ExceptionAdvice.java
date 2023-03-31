package com.cosain.trilo.config.exception;

import com.cosain.trilo.common.dto.ErrorResponse;
import com.cosain.trilo.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final MessageSource messageSource;


    /**
     * 추적하지 못 한 예외
     * -> 이 곳에서 잡힌 예외는 이후 이후, 별도의 커스텀 예외로 정의하거나 별도의 처리 방법을 도입해야한다.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnKnownException(Exception e) {
        log.error("예상치 못 한 예외!", e);

        String errorCode = getMessage("unKnown.code");
        String errorMessage = getMessage("unKnown.message");

        return ErrorResponse.from(errorMessage);
    }

    /**
     * 쿠키 누락 예외
     * TODO: 어떤 쿠키가 누락됐는지 API에 별도의 리스트를 추가해서 전달하면 좋을 것 같다.
     */
    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingCookieError(MissingRequestCookieException e) {
        log.info("쿠키 누락!");
        log.info("debug message = {}", e.getMessage());
        log.info("detail Message Argument = {}", e.getDetailMessageArguments());
        log.info("Cookie Name = {}", e.getCookieName());

        String errorCode = getMessage("MissingRequestCookie.code");
        String errorMessage = getMessage("MissingRequestCookie.message");

        log.info("errorCode={}, errorMessage={}", errorCode, errorMessage);
        return ErrorResponse.from(errorMessage);
    }

    /**
     * 커스텀 예외
     * 개발자에 의해 정의된 커스텀 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        String errorCode = getMessage(e.getErrorName()+".code");
        String errorMessage = getMessage(e.getErrorName()+".message");
        HttpStatus status = e.getHttpStatus();

        log.info("errorCode={}, errorMessage={}, status={}", errorCode, errorMessage, status);

        return ResponseEntity.status(status).body(ErrorResponse.from(errorMessage));
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
