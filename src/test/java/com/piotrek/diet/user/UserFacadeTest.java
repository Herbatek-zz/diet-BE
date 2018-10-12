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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.*;
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

    private Product product;
    private ProductDto productDto;

    private Meal meal;
    private MealDto mealDto;
    private Meal meal2;
    private MealDto mealDto2;

    private User user;

    @BeforeEach
    void setup() {
        initProducts();
        initMeals();
        user = UserSample.johnWithId();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createProduct_whenPrincipalEqualUserId_thenSuccess() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(productService.save(product)).thenReturn(Mono.just(product));
        Mockito.when(productDtoConverter.fromDto(productDto)).thenReturn(product);
        Mockito.when(productDtoConverter.toDto(product)).thenReturn(productDto);

        var created = userFacade.createProduct(user.getId(), productDto).block();

        assertAll(
                () -> assertEquals(productDto, created),
                () -> assertEquals(product.getId(), created.getId()),
                () -> assertEquals(product.getName(), created.getName()),
                () -> assertEquals(product.getDescription(), created.getDescription()),
                () -> assertEquals(product.getProtein(), created.getProtein())
        );

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).save(product);
        verify(productDtoConverter, times(1)).fromDto(productDto);
        verify(productDtoConverter, times(1)).toDto(product);
        verifyNoMoreInteractions(userService, productService, productDtoConverter);
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
        verifyNoMoreInteractions(userService, mealService, mealDtoConverter);
    }

    @Test
    void findAllMealsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        Mockito.doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> userFacade.findAllMealsByUser(user.getId(), PageRequest.of(1, 10)).block());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Find favourite meals, when user has no favourites, then return empty list")
    void findFavouriteMeals_whenUserHasNoFavouriteMeals_thenReturnEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var expected = new Page<>(new ArrayList<MealDto>()
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);


        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        Page<MealDto> block = userFacade.findFavouriteMeals(user.getId(), PageRequest.of(page, pageSize)).block();

        assertAll(
                () -> assertEquals(expected.getPageSize(), block.getPageSize()),
                () -> assertEquals(expected, block),
                () -> assertEquals(expected.getContent(), block.getContent()),
                () -> assertEquals(expected.getTotalElements(), block.getTotalElements()),
                () -> assertEquals(expected.getPageNumber(), block.getPageNumber())
        );

        verify(userService, times(1)).findById(user.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService, mealService, mealDtoConverter);
    }

    @Test
    @DisplayName("Find favourite meals, when user has 2 favourites, then return first page with 2 meals")
    void findFavouriteMeals_whenUserHas2meals_thenReturnFirstPageWith2Meals() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 2;
        var expected = new Page<>(new ArrayList<>(List.of(mealDto, mealDto2))
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        user.setFavouriteMeals(new HashSet<>(Set.of(meal.getId(), meal2.getId())));

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(mealService.findById(meal2.getId())).thenReturn(Mono.just(meal2));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        Mockito.when(mealDtoConverter.toDto(meal2)).thenReturn(mealDto2);

        Page<MealDto> block = userFacade.findFavouriteMeals(user.getId(), PageRequest.of(page, pageSize)).block();

        assertAll(
                () -> assertEquals(expected.getPageSize(), block.getPageSize()),
                () -> assertTrue(expected.getContent().containsAll(block.getContent())),
                () -> assertEquals(expected.getTotalElements(), block.getTotalElements()),
                () -> assertEquals(expected.getPageNumber(), block.getPageNumber())
        );
        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).findById(meal2.getId());
        verify(mealDtoConverter, times(1)).toDto(meal);
        verify(mealDtoConverter, times(1)).toDto(meal2);
        verifyNoMoreInteractions(userService, mealService, mealDtoConverter);
    }

    @Test
    @DisplayName("Add to favourite list, when user has no other meals in favourite, then list size is 1")
    void addToFavourite_whenNoOtherFavourites_thenListSize1() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(1, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService, mealService);
    }

    @Test
    @DisplayName("Add to favourite list other meal, when user has 2 meals in favourite, then list is equal 3")
    void addToFavourite_whenUserHas2ProductsAndWeAddOtherMeal_thenListSizeEquals3() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(userService.save(user)).thenReturn(Mono.just(user));

        user.getFavouriteMeals().add("firstId");
        user.getFavouriteMeals().add("secondId");

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(3, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, mealService);
    }

    @Test
    @DisplayName("Add to favourite existing meal, when user has 2 products, then list size is 2")
    void addToFavourite_whenUserHas2ProductsAndWeAddMealThatIsAlreadyInFavourites_thenListSizeEquals2() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(userService.save(user)).thenReturn(Mono.just(user));

        user.getFavouriteMeals().add("firstId");
        user.getFavouriteMeals().add(meal.getId());

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, mealService);
    }

    @Test
    @DisplayName("Delete meal from favourite list, then user has no longer this meal in favourites")
    void deleteFromFavourites_whenMethodIsInvoked_thenUserHasNoLongerThisMealInFavouriteList() {
        user.setFavouriteMeals(new HashSet<>(Set.of("123sandwich", "321bread", "222potato")));

        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.deleteFromFavourite(user.getId(), "222potato").block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Find by id, when found meal, then return MealDto")
    void findById_whenFound_thenReturnMealDto() {
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        Mockito.when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        MealDto block = userFacade.findMealDtoById(meal.getId()).block();

        assertEquals(mealDto, block);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealDtoConverter, times(1)).toDto(meal);
        verifyNoMoreInteractions(userService, mealDtoConverter);
    }

    @Test
    @DisplayName("Find by id, when not found meal, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        Mockito.when(mealService.findById(meal.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.findMealDtoById(meal.getId()).block());
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Is favourite, when favourites contains checking meal, then return true")
    void isFavourite_whenUserFavouritesContainsCheckingMeal_thenReturnTrue() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        var set = new HashSet<String>();
        set.add(meal.getId());
        user.setFavouriteMeals(set);

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertTrue(block);
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Is favourite, when favourites doesn't contain checking meal, then return true")
    void isFavourite_whenUserFavouritesDoesNotContainCheckingMeal_thenReturnFalse() {
        Mockito.when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        user.setFavouriteMeals(new HashSet<>());

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertFalse(block);
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Is favourite, when meal doesn't exist, then return NotFoundException")
    void isFavourite_whenUserDoesNotExist_thenThrowNotFoundException() {
        final var INVALID_ID = "badid";
        Mockito.when(userService.findById(INVALID_ID)).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> userFacade.isFavourite("badid", meal.getId()));
        verify(userService, times(1)).findById(INVALID_ID);
        verifyNoMoreInteractions(userService);
    }

    private void initMeals() {
        mealDto = dumplingsWithIdDto();
        meal = dumplingsWithId();
        mealDto2 = coffeeWithIdDto();
        meal2 = coffeeWithId();
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

    private void initProducts() {
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
