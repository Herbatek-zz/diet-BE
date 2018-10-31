package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
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
    void beforeEach() {
        initProducts();
        initMeals();
        user = UserSample.johnWithId();
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Find cart, when found cart, then return it")
    void findCart_whenFound_thenReturn() {
        when(cartService.findByUserIdAndDate(user.getId(), cart.getDate())).thenReturn(Mono.just(cartDto));

        final var block = userFacade.findDtoCartByUserAndDate(user.getId(), cart.getDate()).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(cartService, times(1)).findByUserIdAndDate(user.getId(), cart.getDate());
        verifyNoMoreInteractions(cartService);
    }


    @Test
    void createProduct_whenPrincipalEqualUserId_thenSuccess() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.save(productDto)).thenReturn(Mono.just(productDto));

        var created = userFacade.createProduct(user.getId(), productDto).block();

        this.assertEqualsAllProductDtoFields(productDto, created);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).save(productDto);
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
        when(productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var firstPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize));
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
        when(productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var firstPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize));
        verify(productDtoConverter, times(0)).toDto(product);
        verifyNoMoreInteractions(userService, productService, productDtoConverter);
    }

    @Test
    void findAllProductsByUserId_whenUserDoesNotExist_thenThrowNotFoundException() {
        doThrow(BadRequestException.class).when(userService).findById(user.getId());

        assertThrows(BadRequestException.class,
                () -> userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(1, 10)).block());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
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
        verifyNoMoreInteractions(userService, mealService, mealDtoConverter, userValidation);
    }

    @Test
    void createMeal_whenPrincipalNotEqualUserId_thenFailure() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));
        doThrow(BadRequestException.class).when(userValidation).validateUserWithPrincipal(user.getId());

        assertThrows(BadRequestException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verifyNoMoreInteractions(userService, mealService, userValidation);
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
        when(mealService.findAllByUserId(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId(), PageRequest.of(page, pageSize));
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
        when(mealService.findAllByUserId(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var firstPage = userFacade.findAllMealsByUser(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, firstPage);

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findAllByUserId(user.getId(), PageRequest.of(page, pageSize));
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

        user.setFavouriteMeals(new HashSet<>(Set.of(meal, meal2)));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
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
        verify(mealDtoConverter, times(1)).toDto(meal);
        verify(mealDtoConverter, times(1)).toDto(meal2);
        verifyNoMoreInteractions(userService, mealDtoConverter);
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
        verifyNoMoreInteractions(userService, mealService, userValidation);
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
        verifyNoMoreInteractions(userService, mealService, userValidation);
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
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, mealService);
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

        var set = new HashSet<Meal>();
        set.add(meal);
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

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cartDto);
        verifyNoMoreInteractions(cartService, mealService, userValidation);
    }

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart should has 1 meal")
    void addMealToCart_whenThereIsNoCart_thenCreateCartAndAddMeal() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(any(CartDto.class))).thenReturn(Mono.just(cartDto));

        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        final var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(any(CartDto.class));
        verifyNoMoreInteractions(cartService, mealService, userValidation);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one meal, then cart should has 2 meals")
    void addMealToCart_whenCartHadOneMeal_thenCartShouldHasTwoMeals() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cart.getMeals().add(meal);
        cartDto.getMeals().add(mealDto);
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.getAllProducts().addAll(mealDto.getProducts());
        cartDto.getAllProducts().addAll(MealSample.dumplingsWithIdDto().getProducts());

        var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cartDto);
        verifyNoMoreInteractions(cartService, mealService, userValidation);
    }

    @Test
    @DisplayName("Add meal to cart, when cart had one product, then cart should has one product and one meal")
    void addMealToCart_whenCartHadOneProduct_thenCartShouldHasOneMealAndOneProduct() {
        final var meal = MealSample.coffeeWithId();
        final var mealDto = MealSample.coffeeWithIdDto();
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = userFacade.addMealToCart(user.getId(), meal.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).calculateMealInformation(meal);
        verify(cartService, times(1)).save(cartDto);
        verifyNoMoreInteractions(cartService, mealService, userValidation);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had one meal, then cart should be empty")
    void deleteMealFromCart_whenCartHad1Meal_thenCartShouldBeEmpty() {
        final var meal = MealSample.coffeeWithId();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cart.getMeals().add(meal);

        userFacade.deleteMealFromCart(user.getId(), meal.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), cart.getId()),
                () -> assertEquals(cartDto.getDate(), cart.getDate()),
                () -> assertEquals(cartDto.getUserId(), cart.getUserId()),
                () -> assertEquals(0, cart.getProducts().size()),
                () -> assertEquals(0, cart.getMeals().size())
        );
        assertEquals(0, cart.getMeals().size());
        verify(cartService, times(1)).save(cartDto);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verifyNoMoreInteractions(cartService, mealService);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart had no meals, then return empty cart")
    void deleteMealFromCart_whenCartHadNoMeals_thenCartShouldBeEmpty() {
        final var meal = MealSample.coffeeWithId();

        cart.getMeals().add(meal);
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        userFacade.deleteMealFromCart(user.getId(), meal.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), cart.getId()),
                () -> assertEquals(cartDto.getDate(), cart.getDate()),
                () -> assertEquals(cartDto.getUserId(), cart.getUserId()),
                () -> assertEquals(0, cart.getProducts().size()),
                () -> assertEquals(0, cart.getMeals().size())
        );

        assertEquals(0, cart.getMeals().size());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(cartService, times(1)).save(any(CartDto.class));
        verifyNoMoreInteractions(cartService, mealService);
    }

    @Test
    @DisplayName("Add product to cart, when there is no cart, then cart should be created and product should be added")
    void addProductToCart_whenThereIsNoCart_thenCreateCartAndAddProduct() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.error(new NotFoundException("")));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(any(CartDto.class))).thenReturn(Mono.just(cartDto));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(any(CartDto.class));
        verifyNoMoreInteractions(cartService, mealService, userValidation);
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart should has 1 product")
    void addProductToCart_whenCartIsEmpty_thenCartShouldHasOneProduct() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);

        final var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cartDto);
        verifyNoMoreInteractions(cartService, mealService, userValidation);
    }

    @Test
    @DisplayName("Add product to cart, when cart had one product, then cart should has 2 products")
    void addProductToCart_whenCartHadOneProduct_thenCartShouldHasTwoProducts() {
        final var product = ProductSample.bananaWithId();
        final var productDto = ProductSample.bananaWithIdDto();

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cart.getProducts().add(product);
        cartDto.getProducts().add(productDto);
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.getAllProducts().add(productDto);
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());

        var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cartDto);
        verifyNoMoreInteractions(cartService, mealService, userValidation);
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

        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));
        cart.getMeals().add(meal);
        cartDto.getProducts().add(productDto);
        cartDto.getAllProducts().add(productDto);
        cartDto.getMeals().add(mealDto);
        cartDto.getAllProducts().addAll(mealDto.getProducts());

        var block = userFacade.addProductToCart(user.getId(), product.getId(), cart.getDate(), 100).block();

        this.assertEqualsAllCartDtoFields(cartDto, block);
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cartDto);
        verifyNoMoreInteractions(cartService, productService, userValidation);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had one product, then cart should be empty")
    void deleteProductFromCart_whenCartHadOneProduct_thenCartShouldBeEmpty() {
        final var product = ProductSample.bananaWithId();

        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        cart.getProducts().add(product);

        userFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), cart.getId()),
                () -> assertEquals(cartDto.getDate(), cart.getDate()),
                () -> assertEquals(cartDto.getUserId(), cart.getUserId()),
                () -> assertEquals(0, cart.getProducts().size()),
                () -> assertEquals(0, cart.getMeals().size())
        );
        verify(userValidation, times(1)).validateUserWithPrincipal(user.getId());
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).save(cartDto);
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verifyNoMoreInteractions(cartService, productService, userValidation);
    }

    @Test
    @DisplayName("Delete product from cart, when cart had no products, then cart should be empty")
    void deleteProductFromCart_whenCartHadNoProducts_thenCartShouldBeEmpty() {
        final var product = ProductSample.bananaWithId();

        when(productService.findById(product.getId())).thenReturn(Mono.just(product));
        when(cartService.findByUserIdAndDate(cart.getUserId(), cart.getDate())).thenReturn(Mono.just(cartDto));
        when(cartService.save(cartDto)).thenReturn(Mono.just(cartDto));

        userFacade.deleteProductFromCart(user.getId(), product.getId(), cart.getDate()).block();

        assertAll(
                () -> assertEquals(cartDto.getId(), cart.getId()),
                () -> assertEquals(cartDto.getDate(), cart.getDate()),
                () -> assertEquals(cartDto.getUserId(), cart.getUserId()),
                () -> assertEquals(0, cart.getProducts().size()),
                () -> assertEquals(0, cart.getMeals().size())
        );
        verify(productService, times(1)).findById(product.getId());
        verify(cartService, times(1)).findByUserIdAndDate(cart.getUserId(), cart.getDate());
        verifyNoMoreInteractions(cartService, productService);
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

    private void assertEqualsAllCartDtoFields(CartDto expected, CartDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size()),
                () -> assertEquals(expected.getAllProducts(), actual.getAllProducts()),
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
