//package com.piotrek.diet.product;
//
//import com.piotrek.diet.helpers.Page;
//import com.piotrek.diet.helpers.exceptions.BadRequestException;
//import com.piotrek.diet.sample.ProductSample;
//import com.piotrek.diet.sample.UserSample;
//import com.piotrek.diet.user.User;
//import com.piotrek.diet.user.UserService;
//import com.piotrek.diet.user.UserValidation;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.PageRequest;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.ArrayList;
//import java.util.stream.Collectors;
//
//import static com.piotrek.diet.sample.ProductSample.bananaWithId;
//import static com.piotrek.diet.sample.ProductSample.bananaWithIdDto;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//class ProductFacadeTest {
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private ProductDtoConverter productDtoConverter;
//
//    @Mock
//    private UserValidation userValidation;
//
//    @InjectMocks
//    private ProductFacade productFacade;
//
//    private ProductDto productDto;
//    private Product product;
//    private User user;
//
//    @BeforeEach
//    void setup() {
//        createProducts();
//        user = UserSample.johnWithId();
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    void createProduct_whenPrincipalEqualUserId_thenSuccess() {
//
//        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
//        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
//        Mockito.when(productDtoConverter.fromDto(productDto)).thenReturn(product);
//        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);
//
//        ProductDto created = productFacade.createProduct(user.getId(), productDto).block();
//
//        assertEquals(productDto, created);
//    }
//
//    @Test
//    void createProduct_whenPrincipalNotEqualUserId_thenFailure() {
//
//        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
//        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
//        Mockito.doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());
//
//        assertThrows(BadRequestException.class, () -> productFacade.createProduct(user.getId(), productDto).block());
//    }
//
//    @Test
//    void createProduct_whenUserDoesNotExist_thenThrowNotFoundException() {
//        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());
//
//        assertThrows(BadRequestException.class, () -> productFacade.createProduct(user.getId(), productDto).block());
//    }
//
//    @Test
//    void findAllByUserId_whenUserHas20ProductsPage0PageSize10_thenReturnFirstPage() {
//        var page = 0;
//        var pageSize = 10;
//        var totalElements = 20;
//        var productList = createProductList(totalElements);
//        var productDtoList = createProductDtoList(totalElements);
//        var expected = new Page<>(productDtoList
//                .stream()
//                .limit(pageSize)
//                .collect(Collectors.toList()), page, pageSize, totalElements);
//
//        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
//        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);
//        Mockito.when(productService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));
//
//        var firstPage = productFacade.findAllByUserId(user.getId(), PageRequest.of(page, pageSize)).block();
//
//        assertEquals(expected, firstPage);
//
//        verify(userService, times(1)).findById(user.getId());
//        verify(productService, times(1)).findAllByUserId(user.getId());
//        verify(productDtoConverter, times(10)).toDto(product);
//        verifyNoMoreInteractions(userService);
//        verifyNoMoreInteractions(productService);
//        verifyNoMoreInteractions(productDtoConverter);
//    }
//
//    @Test
//    void findAllByUserId_whenUserHasNoProductsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
//        var page = 0;
//        var pageSize = 10;
//        var totalElements = 0;
//        var productList = new ArrayList<Product>();
//        var productDtoList = new ArrayList<ProductDto>();
//        var expected = new Page<>(productDtoList
//                .stream()
//                .limit(pageSize)
//                .collect(Collectors.toList()), page, pageSize, totalElements);
//
//        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
//        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);
//        Mockito.when(productService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));
//
//        var firstPage = productFacade.findAllByUserId(user.getId(), PageRequest.of(page, pageSize)).block();
//
//        assertEquals(expected, firstPage);
//
//        verify(userService, times(1)).findById(user.getId());
//        verify(productService, times(1)).findAllByUserId(user.getId());
//        verify(productDtoConverter, times(0)).toDto(product);
//        verifyNoMoreInteractions(userService);
//        verifyNoMoreInteractions(productService);
//        verifyNoMoreInteractions(productDtoConverter);
//    }
//
//    @Test
//    void findAllByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
//        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());
//
//        assertThrows(BadRequestException.class,
//                () -> productFacade.findAllByUserId(user.getId(), PageRequest.of(1, 10)).block());
//    }
//
//    private void createProducts() {
//        productDto = ProductSample.bananaWithIdDto();
//        product = ProductSample.bananaWithId();
//    }
//
//    private ArrayList<Product> createProductList(int size) {
//        var arrayList = new ArrayList<Product>();
//
//        for (int i = 0; i < size; i++)
//            arrayList.add(bananaWithId());
//
//        return arrayList;
//    }
//
//    private ArrayList<ProductDto> createProductDtoList(int size) {
//        var arrayList = new ArrayList<ProductDto>();
//
//        for (int i = 0; i < size; i++)
//            arrayList.add(bananaWithIdDto());
//
//        return arrayList;
//    }
//}