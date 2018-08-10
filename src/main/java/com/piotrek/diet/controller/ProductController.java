package com.piotrek.diet.controller;

import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductDtoConverter productDtoConverter;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<ProductDto> findById(@PathVariable String id) {
        return null;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        productService.deleteById(id);
        return Mono.empty();
    }
}
