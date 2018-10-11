package com.piotrek.diet.user;

import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.BadRequestException;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.ProductSample.bananaWithId;
import static com.piotrek.diet.sample.ProductSample.bananaWithIdDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserFacadeTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartDtoConverter cartDtoConverter;

    @Mock
    private UserService userService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private ProductService productService;

    @Mock
    private MealService mealService;

    @Mock
    private MealDtoConverter mealDtoConverter;

    @InjectMocks
    private UserFacade userFacade;

    private ProductDto productDto;
    private Product product;
    private User user;

    @BeforeEach
    void setup() {
        createProducts();
        user = UserSample.johnWithId();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createProduct_whenPrincipalEqualUserId_thenSuccess() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.when(productDtoConverter.fromDto(productDto)).thenReturn(product);
        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);

        ProductDto created = userFacade.createProduct(user.getId(), productDto).block();

        assertEquals(productDto, created);
    }

    @Test
    void createProduct_whenPrincipalNotEqualUserId_thenFailure() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
    }

    @Test
    void createProduct_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
    }

    @Test
    void findAllByUserId_whenUserHas20ProductsPage0PageSize10_thenReturnFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var productList = createProductList(totalElements);
        var productDtoList = createProductDtoList(totalElements);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);
        Mockito.when(productService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));

        var firstPage = userFacade.findAllProducts(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(10)).toDto(product);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllByUserId_whenUserHasNoProductsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var productList = new ArrayList<Product>();
        var productDtoList = new ArrayList<ProductDto>();
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);
        Mockito.when(productService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));

        var firstPage = userFacade.findAllProducts(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(0)).toDto(product);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> userFacade.findAllProducts(user.getId(), PageRequest.of(1, 10)).block());
    }

    private void createProducts() {
        productDto = bananaWithIdDto();
        product = bananaWithId();
    }

    private ArrayList<Product> createProductList(int size) {
        var arrayList = new ArrayList<Product>();

        for (int i = 0; i < size; i++)
            arrayList.add(bananaWithId());

        return arrayList;
    }

    private ArrayList<ProductDto> createProductDtoList(int size) {
        var arrayList = new ArrayList<ProductDto>();

        for (int i = 0; i < size; i++)
            arrayList.add(bananaWithIdDto());

        return arrayList;
    }
}
