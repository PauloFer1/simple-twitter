package com.hsbc.simpletwitter.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class SimpleTwitterExceptionHandler {

    @ExceptionHandler(MaxTweetCharactersException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(final MaxTweetCharactersException ex,
                                                   final WebRequest request) {
        log.error(String.format("%s %s", request, ex));
        return ResponseEntity.badRequest().body(
                new ErrorResponse((HttpStatus.BAD_REQUEST.value()), ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(UserCannotFollowHimselfException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(final UserCannotFollowHimselfException ex,
                                                          final WebRequest request) {
        log.error(String.format("%s %s", request, ex));
        return ResponseEntity.badRequest().body(
                new ErrorResponse((HttpStatus.BAD_REQUEST.value()), ex.getMessage(), Instant.now()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ErrorResponse {
        private int httpCode;
        private String errorMessage;
        private Instant timestamp;
    }

}
