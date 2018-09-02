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

    @Mock
    private ProductDtoConverter productDtoConverter;

    @InjectMocks
    private ProductFacade productFacade;

    private ProductDto productDto;
    private Product product;
    private User user;

    @BeforeEach
    void setup() {
        createProducts();
        createUser();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createProduct_whenPrincipalEqualUserId_thenSuccess() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.when(productDtoConverter.fromDto(productDto)).thenReturn(product);
        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);

        ProductDto created = productFacade.createProduct(user.getId(), productDto).block();

        assertEquals(productDto, created);
    }

    @Test
    void createProduct_whenPrincipalNotEqualUserId_thenFailure() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.doThrow(BadRequestException.class).when(userService).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> productFacade.createProduct(user.getId(), productDto).block());
    }

    private void createProducts() {
        productDto = ProductSample.bananaWithIdDto();
        product = ProductSample.bananaWithId();
    }

    private void createUser() {
        user = UserSample.johnWithId();
    }
}