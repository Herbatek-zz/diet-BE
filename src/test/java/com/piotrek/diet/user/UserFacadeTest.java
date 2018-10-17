package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
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
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setup() {
        initProducts();
        initMeals();
        user = UserSample.johnWithId();
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Find cart, when found, then return it")
    void findCart_whenFound_thenReturn() {
        when(cartService.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        final var block = userFacade.findDtoCart(user.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate())
        );
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartDtoConverter);
    }

    @Test
    @DisplayName("Find cart, when found, then return it")
    void findCart_whenNotFound_thenSaveNewCartAndReturn() {
        when(cartService.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.empty());
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        var block = userFacade.findDtoCart(user.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(cart.getMeals().size(), block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate())
        );
    }

    @Test
    @DisplayName("Add meal to today cart, when cart is empty, then cart should has 1 meal")
    void addMealToTodayCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartService.findByUserIdAndDate(user.getId(), LocalDate.now())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cartDto.getMeals().add(mealDto);
        cartDto.getProducts().addAll(mealDto.getProducts());

        CartDto block = userFacade.addMealToTodayCart(user.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(1, block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(2, block.getProducts().size())
        );
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), LocalDate.now());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    @DisplayName("Add meal to today cart, when cart had one meal, then cart should has 2 meals")
    void addMealToTodayCart_whenCartHad1Meal_thenCartShouldHasTwoMeals() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.findByUserIdAndDate(user.getId(), LocalDate.now())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.setDate(LocalDate.now());
        cartDto.setDate(LocalDate.now());
        cart.getMeals().add(meal2);
        cartDto.getMeals().add(mealDto2);
        cartDto.getProducts().addAll(mealDto2.getProducts());
        cartDto.getMeals().add(mealDto);
        cartDto.getProducts().addAll(mealDto.getProducts());

        CartDto block = userFacade.addMealToTodayCart(user.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(cart.getId(), block.getId()),
                () -> assertEquals(2, block.getMeals().size()),
                () -> assertEquals(cart.getUserId(), block.getUserId()),
                () -> assertEquals(cart.getDate(), block.getDate()),
                () -> assertEquals(4, block.getProducts().size())
        );
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), LocalDate.now());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    void deleteMealFromTodayCart_whenCartHad1Meal_thenCartShouldBeEmpty() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.findByUserIdAndDate(user.getId(), LocalDate.now())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getMeals().add(meal);

        CartDto block = userFacade.deleteMealFromTodayCart(user.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(0, block.getMeals().size())
        );

        assertEquals(0, cart.getMeals().size());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), LocalDate.now());
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    void deleteMealFromTodayCart_whenCartHadNoMeals_thenCartShouldBeEmpty() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.findByUserIdAndDate(user.getId(), LocalDate.now())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        CartDto block = userFacade.deleteMealFromTodayCart(user.getId(), meal.getId()).block();

        assertAll(
                () -> assertEquals(0, block.getMeals().size())
        );

        assertEquals(0, cart.getMeals().size());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), LocalDate.now());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, mealService, cartDtoConverter);
    }

    @Test
    void createProduct_whenPrincipalEqualUserId_thenSuccess() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.save(product)).thenReturn(Mono.just(product));
        when(productDtoConverter.fromDto(productDto)).thenReturn(product);
        when(productDtoConverter.toDto(product)).thenReturn(productDto);

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
        doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
        verifyNoMoreInteractions(userService, productService);
    }

    @Test
    void createProduct_whenUserDoesNotExist_thenThrowNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
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

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productDtoConverter.toDto(product)).thenReturn(productDto);
        when(productService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));

        var firstPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(10)).toDto(product);
        verifyNoMoreInteractions(userService, productService, productDtoConverter);
    }

    @Test
    void findAllProductsByUserId_whenUserHasNoProductsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var productList = new ArrayList<Product>();
        var expected = new Page<>(new ArrayList<ProductDto>()
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productDtoConverter.toDto(product)).thenReturn(productDto);
        when(productService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(productList));

        var firstPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserId(user.getId());
        verify(productDtoConverter, times(0)).toDto(product);
        verifyNoMoreInteractions(userService, productService, productDtoConverter);
    }

    @Test
    void findAllProductsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(1, 10)).block());
    }

    @Test
    void createMeal_whenPrincipalEqualUserId_thenSuccess() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.save(mealDto)).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        var created = userFacade.createMeal(user.getId(), mealDto).block();

        assertEquals(mealDto, created);
    }

    @Test
    void createMeal_whenPrincipalNotEqualUserId_thenFailure() {

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));
        doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
    }

    @Test
    void createMeal_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(BadRequestException.class).when(userService).findById(user.getId());

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

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        when(mealService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealsList));

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

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        when(mealService.findAllByUserId(user.getId())).thenReturn(Flux.fromIterable(mealList));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService, mealService, mealDtoConverter);
    }

    @Test
    void findAllMealsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(BadRequestException.class).when(userService).findById(user.getId());

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


        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

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

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealService.findById(meal2.getId())).thenReturn(Mono.just(meal2));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        when(mealDtoConverter.toDto(meal2)).thenReturn(mealDto2);

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
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

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
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

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
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

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

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.deleteFromFavourite(user.getId(), "222potato").block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Find by id, when found meal, then return MealDto")
    void findById_whenFound_thenReturnMealDto() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        MealDto block = userFacade.findMealDtoById(meal.getId()).block();

        assertEquals(mealDto, block);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealDtoConverter, times(1)).toDto(meal);
        verifyNoMoreInteractions(userService, mealDtoConverter);
    }

    @Test
    @DisplayName("Find by id, when not found meal, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.findMealDtoById(meal.getId()).block());
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Is favourite, when favourites contains checking meal, then return true")
    void isFavourite_whenUserFavouritesContainsCheckingMeal_thenReturnTrue() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));

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
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));

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
        when(userService.findById(INVALID_ID)).thenThrow(new NotFoundException(""));

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
