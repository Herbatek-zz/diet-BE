package com.piotrek.diet.controller;

import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public void find(@PathVariable Long id) {
        System.out.println(id);
    }
}
