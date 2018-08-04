package com.piotrek.diet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping
    @ResponseStatus(OK)
    public String successfulLogin() {
        return "Udało sie zalogować!";
    }
}
