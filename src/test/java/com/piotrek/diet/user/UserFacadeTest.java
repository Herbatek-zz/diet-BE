package com.piotrek.diet.user;

import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.exceptions.BadRequestException;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.dumplingsWithId;
import static com.piotrek.diet.sample.MealSample.dumplingsWithIdDto;
import static com.piotrek.diet.sample.ProductSample.bananaWithId;
import static com.piotrek.diet.sample.ProductSample.bananaWithIdDto;
import static org.junit.jupiter.api.Assertions.*;
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
    private MealDto mealDto;
    private Meal meal;
    private User user;

    @BeforeEach
    void setup() {
        createProducts();
        createMeals();
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
    void findAllProductsByUserId_whenUserHas20ProductsPage0PageSize10_thenReturnFirstPage() {
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

        var firstPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(10)).toDto(product);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllProductsByUserId_whenUserHasNoProductsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
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

        var firstPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(0)).toDto(product);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(productDtoConverter);
    }

    @Test
    void findAllProductsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(1, 10)).block());
    }

    @Test
    void createMeal_whenPrincipalEqualUserId_thenSuccess() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.save(mealDto)).thenReturn(Mono.just(meal));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        var created = userFacade.createMeal(user.getId(), mealDto).block();

        assertEquals(mealDto, created);
    }

    @Test
    void createMeal_whenPrincipalNotEqualUserId_thenFailure() {

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.save(meal)).thenReturn(Mono.just(meal));
        Mockito.doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
    }

    @Test
    void createMeal_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
    }

    @Test
    void findAllMealsByUserId_whenUserHas20MealsPage0PageSize10_thenReturnFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var mealsList = createMealsList(totalElements);
        var mealDtoList = createMealDtoList(totalElements);
        var expected = new Page<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        Mockito.when(mealService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealsList));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId());
        verify(mealDtoConverter, times(10)).toDto(meal);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(mealService);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllMealsByUserId_whenUserHasNoMealsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var mealList = new ArrayList<Meal>();
        var expected = new Page<>(new ArrayList<MealDto>()
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        Mockito.when(mealService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealList));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(mealService);
        verifyNoMoreInteractions(mealDtoConverter);
    }

    @Test
    void findAllMealsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> userFacade.findAllMealsByUser(user.getId(), PageRequest.of(1, 10)).block());
    }

//    Mono<Page<MealDto>> findFavouriteMeals(String userId, Pageable pageable) {
//        var favouriteMealListId = requireNonNull(userService.findById(userId).block()).getFavouriteMeals();
//
//        var collect = favouriteMealListId
//                .stream()
//                .skip(pageable.getPageNumber() * pageable.getPageSize())
//                .limit(pageable.getPageSize())
//                .map(mealService::findById)
//                .map(Mono::block)
//                .map(mealDtoConverter::toDto)
//                .collect(Collectors.toList());
//
//        return Mono.just(new Page<>(collect, pageable.getPageNumber(), pageable.getPageSize(), favouriteMealListId.size()));
//    }
//
//    Mono<Void> addToFavourite(String userId, String mealId) {
//        userValidation.validateUserWithPrincipal(userId);
//
//        var user = userService.findById(userId).block();
//        var meal = mealService.findById(mealId).block();
//
//        user.getFavouriteMeals().add(meal.getId());
//
//        return userService.save(user).then();
//    }
//
//    Mono<Void> deleteFromFavourite(String userId, String mealId) {
//        userValidation.validateUserWithPrincipal(userId);
//
//        return userService.findById(userId)
//                .doOnNext(user -> requireNonNull(user).getFavouriteMeals().remove(mealId))
//                .flatMap(userService::save)
//                .then();
//    }
//
//    public Mono<MealDto> findById(String id) {
//        return mealService.findById(id)
//                .map(mealDtoConverter::toDto);
//    }
//

    @Test
    @DisplayName("Is favourite, when favourites contains checking meal, then return true")
    void isFavourite_whenUserFavouritesContainsCheckingMeal_thenReturnTrue() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        var set = new HashSet<String>();
        set.add(meal.getId());
        user.setFavouriteMeals(set);

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertTrue(block);
    }

    @Test
    @DisplayName("Is favourite, when favourites doesn't contain checking meal, then return true")
    void isFavourite_whenUserFavouritesDoesNotContainCheckingMeal_thenReturnFalse() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        user.setFavouriteMeals(new HashSet<>());

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertFalse(block);
    }

    @Test
    @DisplayName("Is favourite, when meal doesn't exist, then return NotFoundException")
    void isFavourite_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.when(userService.findById("badid")).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> userFacade.isFavourite("badid", meal.getId()));
    }

    private void createMeals() {
        mealDto = dumplingsWithIdDto();
        meal = dumplingsWithId();
    }

    private ArrayList<Meal> createMealsList(int size) {
        var arrayList = new ArrayList<Meal>();

        for (int i = 0; i < size; i++)
            arrayList.add(dumplingsWithId());

        return arrayList;
    }

    private ArrayList<MealDto> createMealDtoList(int size) {
        var arrayList = new ArrayList<MealDto>();

        for (int i = 0; i < size; i++)
            arrayList.add(dumplingsWithIdDto());

        return arrayList;
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
