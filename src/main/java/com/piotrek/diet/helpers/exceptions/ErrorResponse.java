package com.piotrek.diet.helpers.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class ErrorResponse {
    private int code;
    private String message;
}
