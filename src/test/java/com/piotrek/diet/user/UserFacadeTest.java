package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.*;
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
import com.piotrek.diet.security.token.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.bananaWithId;
import static com.piotrek.diet.helpers.ProductSample.bananaWithIdDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFacadeTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private CartDtoConverter cartDtoConverter;

    @Mock
    private ProductService productService;

    @Mock
    private MealService mealService;

    @Mock
    private MealDtoConverter mealDtoConverter;

    @Mock
    private TokenService tokenService;

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
    void beforeEach() {
        initProducts();
        initMeals();
        user = UserSample.johnWithId();
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Find cart, when found, then return")
    void findCart_whenFound_thenReturn() {
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        final CartDto block = userFacade.findDtoCartByUserAndDate(cart.getUserId(), cart.getDate()).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Find cart, when not found, then return")
    void findCart_whenNotFound_thenThrowNotFoundException() {
        when(cartService.findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate())).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> userFacade.findDtoCartByUserAndDate(cartDto.getUserId(), cartDto.getDate()).block());

        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }


    @Test
    void createProduct_whenPrincipalEqualUserId_thenSuccess() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.save(productDto)).thenReturn(Mono.just(productDto));

        var created = userFacade.createProduct(user.getId(), productDto).block();

        this.assertEqualsAllProductDtoFields(productDto, created);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).save(productDto);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void createProduct_whenPrincipalNotEqualUserId_thenFailure() {
        doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void createProduct_whenUserDoesNotExist_thenThrowNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
        verify(userService, times(1)).findById(user.getId());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void findAllProductsByUserId_whenUserHas20Products_thenReturnPageWithProducts() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var productDtoList = createProductDtoList(totalElements);
        var expected = new Page<>(productDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var actualPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, actualPage);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize));
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void findAllProductsByUserId_whenUserHasNoProducts_thenReturnEmptyPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var expected = new Page<>(new ArrayList<ProductDto>(), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var actualPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, actualPage);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize));
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void findAllProductsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).findById(user.getId());

        assertThrows(NotFoundException.class,
                () -> userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(1, 10)).block());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void createMeal_whenPrincipalEqualUserId_thenSuccess() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.save(mealDto)).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        var created = userFacade.createMeal(user.getId(), mealDto).block();

        this.assertEqualsAllMealDtoFields(mealDto, created);
        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).save(mealDto);
        verify(mealDtoConverter, times(1)).toDto(meal);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void createMeal_whenPrincipalNotEqualUserId_thenFailure() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void createMeal_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).findById(user.getId());

        assertThrows(NotFoundException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
        verify(userService, times(1)).findById(user.getId());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void findAllMealsByUserId_whenUserHas20Meals_thenReturnFirstPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 20;
        var mealDtoList = createMealDtoList(totalElements);
        var expected = new Page<>(mealDtoList
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList()), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findAllByUserId(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId(), PageRequest.of(page, pageSize));
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void findAllMealsByUserId_whenUserHasNoMealsPage0PageSize10_thenReturnFirstPageWithEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var expected = new Page<>(new ArrayList<MealDto>(), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);
        when(mealService.findAllByUserId(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId(), PageRequest.of(page, pageSize));
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    void findAllMealsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).findById(user.getId());

        assertThrows(NotFoundException.class,
                () -> userFacade.findAllMealsByUser(user.getId(), PageRequest.of(1, 10)).block());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Find favourite meals, when user has no favourites, then return empty list")
    void findFavouriteMeals_whenUserHasNoFavouriteMeals_thenReturnEmptyList() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var expected = new Page<>(new ArrayList<MealDto>(), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));

        var block = userFacade.findFavouriteMeals(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, block);
        verify(userService, times(1)).findById(user.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Find favourite meals, when user has 1 favourites, then return first page with 1 meals")
    void findFavouriteMeals_whenUserHas1meals_thenReturnFirstPageWith1Meals() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 1;
        var expected = new Page<>(new ArrayList<>(Set.of(mealDto)), page, pageSize, totalElements);

        user.setFavouriteMeals(new HashSet<>(Set.of(meal)));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        var block = userFacade.findFavouriteMeals(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, block);
        verify(userService, times(1)).findById(user.getId());
        verify(mealDtoConverter, times(1)).toDto(meal);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add to favourite list, when user has no other meals in favourite, then list size is 1")
    void addToFavourite_whenNoOtherFavourites_thenListSize1() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(1, user.getFavouriteMeals().size());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add to favourite list other meal, when user has 2 meals in favourite, then list is equal 3")
    void addToFavourite_whenUserHas2ProductsAndWeAddOtherMeal_thenListSizeEquals3() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

        user.getFavouriteMeals().add(new Meal(UUID.randomUUID().toString()));
        user.getFavouriteMeals().add(new Meal(UUID.randomUUID().toString()));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(3, user.getFavouriteMeals().size());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add to favourite existing meal, when user has 2 products, then list size is 2")
    void addToFavourite_whenUserHas2ProductsAndWeAddMealThatIsAlreadyInFavourites_thenListSizeEquals2() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

        user.getFavouriteMeals().add(new Meal());
        user.getFavouriteMeals().add(meal);

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Delete from favourites, when user ")
    void deleteFromFavourites_whenMethodIsInvoked_thenUserHasNoLongerThisMealInFavouriteList() {
        final var ID_TO_DELETE = UUID.randomUUID().toString();
        user.setFavouriteMeals(new HashSet<>(Set.of(new Meal(ID_TO_DELETE),
                new Meal(UUID.randomUUID().toString()), new Meal(UUID.randomUUID().toString()))));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.deleteFromFavourite(user.getId(), ID_TO_DELETE).block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
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
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Find by id, when not found meal, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.findMealDtoById(meal.getId()).block());
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Is favourite, when favourites contains checking meal, then return true")
    void isFavourite_whenUserFavouritesContainsCheckingMeal_thenReturnTrue() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        var set = new HashSet<Meal>();
        set.add(meal);
        user.setFavouriteMeals(set);

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertTrue(block);
        verify(userService, times(1)).findById(user.getId());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Is favourite, when favourites doesn't contain checking meal, then return true")
    void isFavourite_whenUserFavouritesDoesNotContainCheckingMeal_thenReturnFalse() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        user.setFavouriteMeals(new HashSet<>());

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertFalse(block);
        verify(userService, times(1)).findById(user.getId());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Is favourite, when user doesn't exist, then return NotFoundException")
    void isFavourite_whenUserDoesNotExist_thenThrowNotFoundException() {
        when(userService.findById(user.getId())).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> userFacade.isFavourite(user.getId(), meal.getId()));
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        final var mealToAdd = MealSample.coffeeWithId();
        final var mealDtoToAdd = MealSample.coffeeWithIdDto();
        final var AMOUNT = 100;

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(mealToAdd.getId())).thenReturn(Mono.just(mealToAdd));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cartDto.getMeals().add(mealDtoToAdd);
        cartDto.getAllProducts().addAll(mealDtoToAdd.getProducts());

        final var block = userFacade.addMealToCart(user.getId(), mealToAdd.getId(), cart.getDate(), AMOUNT).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(userValidation, times(1)).validateUserWithPrincipal(cart.getUserId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).findById(mealToAdd.getId());
        verify(mealService, times(1)).calculateMealInformation(mealToAdd);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenThereIsNoCart_thenCreateCartAndAddMeal() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(any(Cart.class));
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one meal, then cart should has 2 meals")
    void addMealToCart_whenCartHadOneMeal_thenCartShouldHasTwoMeals() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getMeals().add(meal);
        cartDto.getMeals().add(mealDto);
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.getAllProducts().addAll(mealDto.getProducts());
        cartDto.getAllProducts().addAll(MealSample.dumplingsWithIdDto().getProducts());

        var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one product, then cart should has one product and one meal")
    void addMealToCart_whenCartHadOneProduct_thenCartShouldHasOneMealAndOneProduct() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had one meal, then cart should be empty")
    void deleteMealFromCart_whenCartHad1Meal_thenCartShouldBeEmpty() {
        final var expected = CartSample.cartDto1();

        when(cartService.findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = userFacade.deleteMealFromCart(cart.getUserId(), mealDto.getId(), cart.getDate()).block();

        this.assertEqualsAllCartFields(expected, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(cartDto.getUserId());
        verify(cartService, times(1)).findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had no meals, then return empty cart")
    void deleteMealFromCart_whenCartHadNoMeals_thenCartShouldBeEmpty() {
        var expected = CartSample.cartDto1();
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = userFacade.deleteMealFromCart(cartDto.getUserId(), meal.getId(), cartDto.getDate()).block();

        this.assertEqualsAllCartFields(expected, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(cartDto.getUserId());
        verify(cartService, times(1)).findByUserIdAndDate(cartDto.getUserId(), cartDto.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add product to cart, when there is no cart, then cart should be created and product should be added")
    void addProductToCart_whenThereIsNoCart_thenCreateCartAndAddProduct() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(any(Cart.class))).thenReturn(Mono.just(cart));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(any(Cart.class));
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart should has 1 product")
    void addProductToCart_whenCartIsEmpty_thenCartShouldHasOneProduct() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one product, then cart should has 2 products")
    void addProductToCart_whenCartHadOneProduct_thenCartShouldHasTwoProducts() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(productDtoConverter.toDto(product)).thenReturn(productDto);
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.getAllProducts().add(productDto);
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());

        var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one meal, then cart should has one product and one meal")
    void addProductToCart_whenCartHadOneMeal_thenCartShouldHasOneMealAndOneProduct() {
        final int amount = 100;

        final var meal = MealSample.coffeeWithId();
        meal.setAmount(amount);

        final var mealDto = MealSample.coffeeWithIdDto();
        mealDto.setAmount(amount);

        final var product = ProductSample.bananaWithId();
        product.setAmount(amount);

        final var productDto = ProductSample.bananaWithIdDto();
        productDto.setAmount(amount);

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        cart.getMeals().add(meal);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had one product, then cart should be empty")
    void deleteProductFromCart_whenCartHadOneProduct_thenCartShouldBeEmpty() {
        cart.getProducts().add(product);

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);

        CartDto block = userFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        var expected = CartSample.cartDto1();
        expected.setProducts(new ArrayList<>());

        this.assertEqualsAllCartFields(expected, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(cartDto.getUserId());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cart);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had no products, then cart should be empty")
    void deleteProductFromCart_whenCartHadNoProducts_thenCartShouldBeEmpty() {
        final var product = ProductSample.bananaWithId();

        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartDtoConverter.toDto(cart)).thenReturn(cartDto);
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cart));
        when(cartService.save(cart)).thenReturn(Mono.just(cart));

        userFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), cart.getId()),
                () -> assertEquals(cartDto.getDate(), cart.getDate()),
                () -> assertEquals(cartDto.getUserId(), cart.getUserId()),
                () -> assertEquals(0, cart.getProducts().size()),
                () -> assertEquals(0, cart.getMeals().size())
        );
        verify(productService, times(1)).findById(product.getId());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartDtoConverter, times(1)).toDto(cart);
        verifyNoMoreInteractions(cartService, userService, userValidation, productDtoConverter,
                productService, mealService, mealDtoConverter, tokenService);
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

    private void assertEqualsAllCartFields(CartDto expected, CartDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size(), "Cart meal list size"),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Cart products list size"),
                () -> assertEquals(expected.getAllProducts(), actual.getAllProducts(), "Cart all products list"),
                () -> assertEquals(expected.getUserId(), actual.getUserId()),
                () -> assertEquals(expected.getDate(), actual.getDate())
        );
    }

    private void assertEqualsAllProductDtoFields(ProductDto expected, ProductDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }

    private void assertEqualsAllMealDtoFields(MealDto expected, MealDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getUserId(), actual.getUserId()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size())
        );
    }


}
