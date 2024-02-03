package ru.socialnet.team43.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.socialnet.team43.exception.RefreshTokenException;

@RestControllerAdvice
public class WebAppExceptionHandler {
    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> refreshTokenExceptionHandler(
            RefreshTokenException exception, WebRequest webRequest) {
        return buildResponse(HttpStatus.FORBIDDEN, exception, webRequest);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(
            HttpStatus httpStatus, Exception exception, WebRequest webRequest) {
        return ResponseEntity.status(httpStatus)
                .body(
                        ErrorResponseBody.builder()
                                .message(exception.getMessage())
                                .description(webRequest.getDescription(false))
                                .build());
    }
}
