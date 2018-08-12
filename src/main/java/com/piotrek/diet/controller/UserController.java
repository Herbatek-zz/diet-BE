package com.piotrek.diet.controller;

import com.piotrek.diet.facade.ProductFacade;
import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.UserDto;
import com.piotrek.diet.model.dto.converter.UserDtoConverter;
import com.piotrek.diet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final ProductFacade productFacade;

    @GetMapping("{id}")
    @ResponseStatus(OK)
    Mono<UserDto> findUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(userDtoConverter::toDto);
    }

    @GetMapping("{id}/products")
    @ResponseStatus(OK)
    Flux<ProductDto> findUserProducts(@PathVariable String id) {
        return null;
    }

    @PostMapping("{id}/products")
    @ResponseStatus(CREATED)
    Mono<ProductDto> saveProduct(@PathVariable String id, @Valid @RequestBody ProductDto productDto) {
        System.out.println(productDto);
        return productFacade.saveProduct(id, productDto);
    }
}
