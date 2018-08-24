package com.piotrek.diet.facade;

import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.User;
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

    public Mono<Product> saveProduct(String userId, Product product) {
        User user = userService.findById(userId).block();

        product.setUserId(user.getId());

        return productService.save(product);
    }
}
