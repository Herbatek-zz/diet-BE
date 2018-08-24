package com.piotrek.diet.facade;

import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.repository.pagination.PageSupport;
import com.piotrek.diet.service.ProductService;
import com.piotrek.diet.service.UserService;
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

    public Mono<Product> saveProduct(String userId, Product product) {
        User user = userService.findById(userId).block();

        product.setUserId(user.getId());

        return productService.save(product);
    }

    public Mono<PageSupport<ProductDto>> findAllByUser(String userId, Pageable pageable) {
        userService.findById(userId).block();

        return productService
                .findAllByUserId(userId)
                .map(productDtoConverter::toDto)
                .collectList()
                .map(list -> new PageSupport<>(
                        list
                                .stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize())
                                .collect(Collectors.toList()),
                        pageable.getPageNumber(), pageable.getPageSize(), list.size()));
    }
}
