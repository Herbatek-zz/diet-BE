package com.piotrek.diet.controller;

import com.piotrek.diet.facade.ProductFacade;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.UserDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.model.dto.converter.UserDtoConverter;
import com.piotrek.diet.repository.pagination.PageSupport;
import com.piotrek.diet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.piotrek.diet.repository.pagination.PageSupport.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.repository.pagination.PageSupport.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final ProductDtoConverter productDtoConverter;
    private final ProductFacade productFacade;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<UserDto> findUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(userDtoConverter::toDto);
    }

    @GetMapping("/{id}/products")
    @ResponseStatus(OK)
    Mono<PageSupport<ProductDto>> findUserProducts(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return productFacade.findAllByUser(id, PageRequest.of(page, size));
    }

    @PostMapping("/{id}/products")
    @ResponseStatus(CREATED)
    Mono<ProductDto> saveProduct(@PathVariable String id, @Valid @RequestBody ProductDto productDto) {
        return productFacade.saveProduct(id, productDtoConverter.fromDto(productDto))
                .map(productDtoConverter::toDto);
    }
}
