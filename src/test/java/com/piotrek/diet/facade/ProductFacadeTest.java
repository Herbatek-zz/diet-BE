package com.piotrek.diet.facade;

import com.piotrek.diet.model.Product;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.dto.ProductDto;
import com.piotrek.diet.model.dto.converter.ProductDtoConverter;
import com.piotrek.diet.sample.SampleProduct;
import com.piotrek.diet.sample.SampleUser;
import com.piotrek.diet.service.ProductService;
import com.piotrek.diet.service.UserService;
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
        product = SampleProduct.bananaWithId();
    }
    private void createUser() {
        user = SampleUser.johnWithId();
    }
}