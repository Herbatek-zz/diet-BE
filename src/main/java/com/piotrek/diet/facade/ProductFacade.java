package com.piotrek.diet.facade;

import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.service.ProductService;
import com.piotrek.diet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final UserService userService;
    private final ProductService productService;
    private final ProductDtoConverter productDtoConverter;

    public Mono<ProductDto> saveProduct(String userId, ProductDto productDto) {
        User user = userService.findById(userId).block();
        Product product = productDtoConverter.fromDto(productDto);

        product.setUserId(user.getId());

        return productService.save(product)
                .map(productDtoConverter::toDto);
    }
}
