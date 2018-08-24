package com.piotrek.diet.product;

import com.piotrek.diet.helpers.PageSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.piotrek.diet.helpers.PageSupport.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.PageSupport.FIRST_PAGE_NUM;
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
        return productService.findById(id)
                .map(productDtoConverter::toDto);
    }

    @GetMapping
    @ResponseStatus(OK)
    Mono<PageSupport<ProductDto>> findAll(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return productService.findAll(PageRequest.of(page, size));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        return productService.deleteById(id);
    }
}
