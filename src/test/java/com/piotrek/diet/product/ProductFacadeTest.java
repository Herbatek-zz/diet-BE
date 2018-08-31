package com.piotrek.diet.product;

import com.piotrek.diet.helpers.exceptions.BadRequestException;
import com.piotrek.diet.sample.ProductSample;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void saveProduct_whenPrincipalEqualUserId_thenSuccess() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.when(userService.isPrincipalIdEqualUserId(user.getId())).thenReturn(true);

        Product created = productFacade.saveProduct(user.getId(), product).block();

        assertEquals(product, created);
    }

    @Test
    void saveProduct_whenPrincipalNotEqualUserId_thenFailure() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.when(userService.isPrincipalIdEqualUserId(user.getId())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> productFacade.saveProduct(user.getId(), product).block());
    }

    private void createProducts() {
        product = ProductSample.bananaWithId();
    }

    private void createUser() {
        user = UserSample.johnWithId();
    }
}