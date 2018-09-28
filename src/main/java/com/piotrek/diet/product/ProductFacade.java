package com.piotrek.diet.product;

import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.user.UserService;
import com.piotrek.diet.user.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final UserService userService;
    private final ProductService productService;
    private final ProductDtoConverter productDtoConverter;
    private final UserValidation userValidation;

    public Mono<ProductDto> createProduct(String userId, ProductDto productDto) {
        userValidation.validateUserWithPrincipal(userId);
        var user = userService.findById(userId).block();
        var product = productDtoConverter.fromDto(productDto);
        product.setUserId(user.getId());

        return productService.save(product).map(productDtoConverter::toDto);
    }

    public Mono<PageSupport<ProductDto>> findAllByUserId(String userId, Pageable pageable) {
        userService.findById(userId).block();

        return productService
                .findAllByUserId(userId)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .map(productDtoConverter::toDto)
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }
}
