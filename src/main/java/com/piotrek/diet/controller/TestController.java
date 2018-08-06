package com.piotrek.diet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping
public class TestController {

    @GetMapping
    @ResponseStatus(OK)
    public String dashBoard() {
        return "Siemanko! </ br>" +
                "Zobacz <a href=\"/users/1\">usera</a>";
    }

    @GetMapping("/login")
    @ResponseStatus(OK)
    public String successfulLogin() {
        return "Zaloguj się na: <a href=\"/login/facebook\">Facebook</a>";
    }
}
