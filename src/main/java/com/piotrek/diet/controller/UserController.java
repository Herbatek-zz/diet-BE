package com.piotrek.diet.controller;

import com.piotrek.diet.model.dto.UserDto;
import com.piotrek.diet.model.dto.converter.UserDtoConverter;
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
    private final UserDtoConverter userDtoConverter;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<UserDto> findUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(userDtoConverter::toDto);
    }
}
