package com.piotrek.diet.controller;

import com.piotrek.diet.model.User;
import com.piotrek.diet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<User> findUserById(@PathVariable String id) {
        return userService.findById(id);
    }
}
