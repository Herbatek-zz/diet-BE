package com.piotrek.diet.product;

import com.piotrek.diet.user.User;
import com.piotrek.diet.sample.ProductSample;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class ProductFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductFacade productFacade;

    private Product product;
    private User user;

    @BeforeEach
    void setup() {
        createProducts();
        createUser();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveProduct() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));

        Product created = productFacade.saveProduct(user.getId(), product).block();

        assertEquals(product, created);
    }

    private void createProducts() {
        product = ProductSample.bananaWithId();
    }
    private void createUser() {
        user = UserSample.johnWithId();
    }
}