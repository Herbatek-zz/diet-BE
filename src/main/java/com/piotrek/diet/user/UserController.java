package com.piotrek.diet.user;

import com.piotrek.diet.product.ProductFacade;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.helpers.PageSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.piotrek.diet.helpers.PageSupport.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.PageSupport.FIRST_PAGE_NUM;
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
        return productFacade.findAllByUserId(id, PageRequest.of(page, size));
    }

    @PostMapping("/{id}/products")
    @ResponseStatus(CREATED)
    Mono<ProductDto> saveProduct(@PathVariable String id, @Valid @RequestBody ProductDto productDto) {
        return productFacade.saveProduct(id, productDtoConverter.fromDto(productDto))
                .map(productDtoConverter::toDto);
    }
}
