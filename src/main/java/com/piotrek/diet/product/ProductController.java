package com.piotrek.diet.product;

import com.piotrek.diet.helpers.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.piotrek.diet.helpers.Page.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.Page.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    Mono<ProductDto> findById(@PathVariable String id) {
        return productService.findDtoById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    Mono<Page<ProductDto>> searchByName(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "") String query) {
        return productService.searchByName(PageRequest.of(page, size), query);
    }

    @GetMapping
    @ResponseStatus(OK)
    Mono<Page<ProductDto>> findAll(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return productService.findAllPageable(PageRequest.of(page, size));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        return productService.deleteById(id);
    }
}
