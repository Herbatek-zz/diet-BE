package com.piotrek.diet.exceptions;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse notFoundHandler(NotFoundException notFoundException) {
        return new ErrorResponse(404, notFoundException.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse accessDeniedHandler(AccessDeniedException exception) {
        return new ErrorResponse(401, exception.getMessage());
    }
}
