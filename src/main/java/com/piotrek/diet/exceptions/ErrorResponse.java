package com.piotrek.diet.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class ErrorResponse {
    private int code;
    private String message;
}
