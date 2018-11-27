package com.piotrek.diet.user;

import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.exceptions.BadRequestException;
import com.piotrek.diet.exceptions.NotFoundException;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.UserSample;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertMealFields;
import static com.piotrek.diet.helpers.AssertEqualAllFields.assertProductFields;
import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.banana;
import static com.piotrek.diet.helpers.ProductSample.bananaDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFacadeTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

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
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        initProducts();
        initMeals();
        user = UserSample.john();
        userDto = UserSample.johnDto();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createProduct_whenFoundUser_thenSuccessfullyCreate() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.save(productDto)).thenReturn(Mono.just(productDto));

        var created = userFacade.createProduct(user.getId(), productDto).block();

        assertProductFields(productDto, created);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).save(productDto);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void createProduct_whenNotFoundUser_thenThrowNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.createProduct(user.getId(), productDto).block());
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void createMeal_whenFoundUser_thenSuccessfullyCreate() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.save(mealDto)).thenReturn(Mono.just(meal));
        when(mealDtoConverter.toDto(meal)).thenReturn(mealDto);

        var created = userFacade.createMeal(user.getId(), mealDto).block();

        assertMealFields(mealDto, created);
        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).save(mealDto);
        verify(mealDtoConverter, times(1)).toDto(meal);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void createMeal_whenNotFoundUser_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).findById(user.getId());

        assertThrows(NotFoundException.class, () -> userFacade.createMeal(user.getId(), mealDto).block());
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void findAllProductsByUserId_whenUserHas20ProductsPageSize10Page0_thenReturnPageWithProducts() {
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

        var foundPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, foundPage);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize));
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void findAllProductsByUserId_whenUserHasNoProducts_thenReturnEmptyPage() {
        var page = 0;
        var pageSize = 10;
        var totalElements = 0;
        var expected = new Page<>(new ArrayList<ProductDto>(), page, pageSize, totalElements);

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(productService.findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize))).thenReturn(Mono.just(expected));

        var foundPage = userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(page, pageSize)).block();

        assertEquals(expected, foundPage);
        verify(userService, times(1)).findById(user.getId());
        verify(productService, times(1)).findAllByUserPageable(user.getId(), PageRequest.of(page, pageSize));
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void findAllProductsByUserId_whenNotFoundUser_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).findById(user.getId());

        assertThrows(NotFoundException.class,
                () -> userFacade.findAllProductsByUserId(user.getId(), PageRequest.of(1, 10)).block());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
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
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
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
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    void findAllMealsByUserId_whenNotFoundUser_thenThrowNotFoundException() {
        doThrow(NotFoundException.class).when(userService).findById(user.getId());

        assertThrows(NotFoundException.class,
                () -> userFacade.findAllMealsByUser(user.getId(), PageRequest.of(1, 10)).block());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
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
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
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
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Add to favourite list, when user has no favourite meals, then list size is one")
    void addToFavourite_whenUserHasNoFavourites_thenListSizeEqualOne() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(1, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).save(meal);
        verify(userService, times(1)).save(user);
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Add to favourite list, when user has no favourite meals, then list size is one")
    void addToFavourite_whenMealIsFirstTimeAddedToFavourites_thenFavouriteCounterEqualOne() {
        meal.setFavouriteCounter(new AtomicLong(0));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(1, meal.getFavouriteCounter().intValue());
        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).save(meal);
        verify(userService, times(1)).save(user);
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Add to favourite list, when user has no favourite meals, then list size is one")
    void addToFavourite_whenMealIsSecondTimeAddedToFavourites_thenFavouriteCounterEqualTwo() {
        User user2 = UserSample.baileyWithId();
        meal.setFavouriteCounter(new AtomicLong(0));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(userService.findById(user2.getId())).thenReturn(Mono.just(user2));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));
        when(userService.save(user2)).thenReturn(Mono.just(user2));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();
        userFacade.addToFavourite(user2.getId(), meal.getId()).block();

        assertEquals(2, meal.getFavouriteCounter().intValue());
        verify(userService, times(2)).findById(anyString());
        verify(mealService, times(2)).findById(meal.getId());
        verify(mealService, times(2)).save(meal);
        verify(userService, times(2)).save(any(User.class));
        verify(mealDtoConverter, times(0)).toDto(meal);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Add to favourite list, when user has 2 favourite meals, then list equals 3")
    void addToFavourite_whenUserHasTwoFavouriteMeals_thenListSizeEqualThree() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(userService.save(user)).thenReturn(Mono.just(user));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));

        user.getFavouriteMeals().add(new Meal(UUID.randomUUID().toString()));
        user.getFavouriteMeals().add(new Meal(UUID.randomUUID().toString()));

        userFacade.addToFavourite(user.getId(), meal.getId()).block();

        assertEquals(3, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).save(meal);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Add to favourite, when add meal which is already in user favourites, then throw BadRequestException")
    void addToFavourite_whenUserHasTwoFavouriteMealsAndWeAddMealWhichIsAlreadyInFavourite_thenThrowException() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));

        user.getFavouriteMeals().add(new Meal());
        user.getFavouriteMeals().add(meal);

        assertThrows(BadRequestException.class, () -> userFacade.addToFavourite(user.getId(), meal.getId()).block());

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Add to favourite, when add meal which is already in user favourites, then the meal favouriteCounter has the same value")
    void addToFavourite_whenAddMealWhichIsAlreadyInFavourite_thenFavouriteCounterHasTheSameValue() {
        user.getFavouriteMeals().add(new Meal());
        user.getFavouriteMeals().add(meal);
        meal.setFavouriteCounter(new AtomicLong(0));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));

        assertThrows(BadRequestException.class, () -> userFacade.addToFavourite(user.getId(), meal.getId()).block());
        assertEquals(0, meal.getFavouriteCounter().intValue());

        verify(userService, times(1)).findById(user.getId());
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Delete from favourites, when user ")
    void deleteFromFavourites_whenUserHasThreeFavouriteMeals_thenUserHasTwoFavouriteMeals() {
        final var ID_TO_DELETE = meal.getId();
        user.setFavouriteMeals(new HashSet<>(Set.of(meal,
                new Meal(UUID.randomUUID().toString()), new Meal(UUID.randomUUID().toString()))));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(userService.save(user)).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.just(meal));
        when(mealService.save(meal)).thenReturn(Mono.just(meal));

        userFacade.deleteFromFavourite(user.getId(), ID_TO_DELETE).block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verify(mealService, times(1)).save(meal);
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Delete from favourites, when user ")
    void deleteFromFavourites_whenMethodIsInvoked_thenUserHasNoLongerThisMealInFavouriteListaaa() {
        final var ID_TO_DELETE = meal.getId();
        user.setFavouriteMeals(new HashSet<>(Set.of(meal,
                new Meal(UUID.randomUUID().toString()), new Meal(UUID.randomUUID().toString()))));

        when(userService.findById(user.getId())).thenReturn(Mono.just(user));
        when(userService.save(user)).thenReturn(Mono.just(user));
        when(mealService.findById(meal.getId())).thenReturn(Mono.error(new NotFoundException("")));

        userFacade.deleteFromFavourite(user.getId(), ID_TO_DELETE).block();

        assertEquals(2, user.getFavouriteMeals().size());
        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).save(user);
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
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
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Find by id, when not found meal, then throw NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        when(mealService.findById(meal.getId())).thenReturn(Mono.error(new NotFoundException("")));

        assertThrows(NotFoundException.class, () -> userFacade.findMealDtoById(meal.getId()).block());
        verify(mealService, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
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
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Is favourite, when favourites doesn't contain checking meal, then return true")
    void isFavourite_whenUserFavouritesDoesNotContainCheckingMeal_thenReturnFalse() {
        when(userService.findById(user.getId())).thenReturn(Mono.just(user));

        user.setFavouriteMeals(new HashSet<>());

        Boolean block = userFacade.isFavourite(user.getId(), meal.getId()).block();

        assertFalse(block);
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    @Test
    @DisplayName("Is favourite, when user doesn't exist, then return NotFoundException")
    void isFavourite_whenUserDoesNotExist_thenThrowNotFoundException() {
        when(userService.findById(user.getId())).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> userFacade.isFavourite(user.getId(), meal.getId()));
        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService, productService, mealService, mealDtoConverter, cartService, tokenService);
    }

    private void initMeals() {
        mealDto = dumplingsDto();
        meal = dumplings();
        mealDto2 = coffeeDto();
        meal2 = coffee();
    }

    private ArrayList<MealDto> createMealDtoList(int size) {
        var arrayList = new ArrayList<MealDto>();

        for (int i = 0; i < size; i++)
            arrayList.add(dumplingsDto());

        return arrayList;
    }

    private void initProducts() {
        productDto = bananaDto();
        product = banana();
    }

    private ArrayList<ProductDto> createProductDtoList(int size) {
        var arrayList = new ArrayList<ProductDto>();

        for (int i = 0; i < size; i++)
            arrayList.add(bananaDto());

        return arrayList;
    }
}
