package com.piotrek.diet.facade;

import com.piotrek.diet.service.ProductService;
import com.piotrek.diet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final UserService userService;
    private final ProductService productService;
}
