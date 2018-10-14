package com.piotrek.diet.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.MealSample;
import com.piotrek.diet.sample.ProductSample;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static com.piotrek.diet.sample.MealSample.dumplingsWithIdDto;
import static com.piotrek.diet.sample.MealSample.dumplingsWithoutIdDto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDtoConverter userDtoConverter;

    @Autowired
    private CartDtoConverter cartDtoConverter;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private MealService mealService;

    @Autowired
    private CartService cartService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private UserDto userDto;

    private Meal meal;
    private MealDto mealDto;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        userService.deleteAll().block();
        createUser();
        createMeal();
        createCart();
        webTestClient = WebTestClient
                .bindToController(new UserController(userService, userFacade))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        userService.deleteAll().block();
    }


    @Test
    @DisplayName("Check if user has meal in favourites, if he has, then return true")
    void isMealFavourite_whenUserHasAMealInFavourites_thenReturnTrue() {
        final var URI = "/users/" + user.getId() + "/meals/" + meal.getId() + "/favourites";

        user.getFavouriteMeals().add(meal.getId());
        userService.save(user).block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Check if user has meal in favourites, if he has no favourites, then return false")
    void isMealFavourite_whenUserHasNoTheMealInFavourites_thenReturnTrue() {
        final var URI = "/users/" + user.getId() + "/meals/" + meal.getId() + "/favourites";
        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    @DisplayName("Find all favourites meals for user, when user has favourites, then return list")
    void findFavouritesMeals_whenUserHasFavourites_thenReturnList() throws JsonProcessingException {
        final var URI = "/users/" + user.getId() + "/meals/favourites";
        final var list = new ArrayList<MealDto>();
        list.add(mealDto);

        user.getFavouriteMeals().add(mealDto.getId());
        userService.save(user).block();

        var expected = new Page<>(list, 0, 10, list.size());

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all favourites meals for user, when user has no favourites, then return empty list")
    void findFavouritesMeals_whenUserHasNoFavourites_thenReturnEmptyList() throws JsonProcessingException {
        final var URI = "/users/" + user.getId() + "/meals/favourites";

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(new Page<>(new ArrayList<MealDto>(), 0, 10, 0)));

    }
//
//    @GetMapping("/{id}/carts")
//    @ResponseStatus(OK)
//    Mono<CartDto> getOrCreateCart(@PathVariable String id, @RequestParam @DateTimeFormat(pattern = "dd-mm-yyyy") LocalDate date) {
//        return userFacade.findDtoCart(id, date);
//    }

    @Test
    @DisplayName("Get or create cart, when found cart, then return it")
    void getOrCreateCart_whenFoundCart_thenReturnIt() {
        final var URI = "/users/" + user.getId() + "/carts?date=03-03-1995";

        cart.setDate(LocalDate.of(1995,Month.MARCH, 3));
        cartDto = cartDtoConverter.toDto(cartService.save(cart).block());

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class).isEqualTo(cartDto);
    }

    @Test
    @DisplayName("Get or create cart, when not found cart, then create new and return")
    void getOrCreateCart_whenNotFoundCart_thenCreateAndReturn() {
        final var URI = "/users/" + user.getId() + "/carts?date=04-03-1995";

        cartDto.setDate(LocalDate.of(1995, Month.MARCH, 4));

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.userId").isEqualTo(cartDto.getUserId())
                .jsonPath("$.date").isEqualTo("04-03-1995")
                .jsonPath("$.products").isEmpty()
                .jsonPath("$.meals").isEmpty()
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    @DisplayName("Add meal to today cart, when cart is empty, then cart should has 1 meal")
    void addMealToTodayCart_whenCartIsEmpty_thenCartShouldHasOneMeal() {
        final var URI = "/users/" + user.getId() + "/carts?mealId=" + meal.getId();

        cart.getMeals().add(meal);
        final var expected = cartDtoConverter.toDto(cart);

        webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class).isEqualTo(expected);
    }

    @Test
    @DisplayName("Add meal to today cart, when cart had one meal and we add the same meal again, then cart should has 2 the same meals")
    void addMealToTodayCart_whenCartHad1MealAndWeAddTheSameMealAgain_thenCartShouldHasTwoTheSameMeals() {
        final var URI = "/users/" + user.getId() + "/carts?mealId=" + meal.getId();

        cart.getMeals().add(meal);

        final var expected = cartDtoConverter.toDto(cart);

        webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class).isEqualTo(expected);

        cart.getMeals().add(meal);

        final var expected2 = cartDtoConverter.toDto(cart);

        webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class).isEqualTo(expected2);
    }

    @Test
    @DisplayName("Add meal to today cart, when cart doesn't exist, then create new cart and return in with one meal")
    void addMealToTodayCart_whenCartDoesNotExist_thenCreateCartAndReturnInWithOneMeal() throws JsonProcessingException {
        final var URI = "/users/" + user.getId() + "/carts?mealId=" + meal.getId();

        cartService.deleteAll().block();
        cart.getMeals().add(meal);

        final var expected = cartDtoConverter.toDto(cart);

        var actual = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult().getResponseBody();

        assertAll(
                () -> assertEquals(expected.getUserId(), actual.getUserId()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size()),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size()),
                () -> assertEquals(expected.getDate(), actual.getDate())
        );
    }

    @Test
    @DisplayName("Add meal to favourites, when user had no favourites, after add he has 1")
    void addMealToFavourites_whenUserHasEmptyList_afterMethodInvokedHeHas1Meal() throws JsonProcessingException {
        final var ADD_URI = "/users/" + user.getId() + "/meals/" + meal.getId() + "/favourites";
        final var FIND_URI = "/users/" + user.getId() + "/meals/favourites";

        final var list = new ArrayList<MealDto>(1);
        list.add(mealDto);

        webTestClient.get().uri(FIND_URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(new Page<>(new ArrayList<MealDto>(), 0, 10, 0)));

        webTestClient.post().uri(ADD_URI)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get().uri(FIND_URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(new Page<>(list, 0, 10, list.size())));
    }

    @Test
    @DisplayName("Delete meal from favourites, then user has no more this meal in favourites")
    void deleteMeaLFromFavourites_thenUserHasNoLongerThisMealInFavourites() throws JsonProcessingException {
        final var DELETE_URI = "/users/" + user.getId() + "/meals/" + meal.getId() + "/favourites";
        final var FIND_URI = "/users/" + user.getId() + "/meals/favourites";

        final var list = new ArrayList<MealDto>(1);
        list.add(mealDto);

        user.getFavouriteMeals().add(meal.getId());
        userService.save(user).block();

        webTestClient.get().uri(FIND_URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(new Page<>(list, 0, 10, list.size())));

        webTestClient.delete().uri(DELETE_URI)
                .exchange()
                .expectStatus().isNoContent();


        webTestClient.get().uri(FIND_URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(new Page<>(new ArrayList<MealDto>(), 0, 10, 0)));
    }

    @Test
    @DisplayName("Find a user by id, when found the user, then return userDto")
    void findUserById_whenUserExists_thenReturnUserDto() {
        final var URI = "/users/" + user.getId();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(UserDto.class)
                .isEqualTo(userDto);
    }

    @Test
    @DisplayName("Find a user by id, when not found, then return 404 not found status")
    void findUserById_whenUserDoesNotExist_thenReturn404() {
        final var URI = "/users/badId";

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findUserProducts_whenUserHasNoProducts_thenReturnPageSupportWithoutElements() {
        webTestClient.get().uri("/users/" + user.getId() + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Page.class)
                .isEqualTo(new Page<>(new ArrayList<>(), 0, 10, 0));
    }

    @Test
    void findUserProducts_whenUserHasProducts_thenReturnArray() throws JsonProcessingException {
        final var URI = "/users/" + user.getId() + "/products";
        final var products = new ArrayList<ProductDto>(2);
        products.add(userFacade.createProduct(user.getId(), ProductSample.bananaWithoutIdDto()).block());
        products.add(userFacade.createProduct(user.getId(), ProductSample.breadWithoutIdDto()).block());

        final var expected = new Page<>(products, 0, 10, products.size());

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void createProduct_whenAllRequiredField_thenSaveProductAndReturnDto() {
        final var URI = "/users/" + user.getId() + "/products";
        final var productDto = ProductSample.bananaWithIdDto();
        productDto.setUserId(user.getId());

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .isEqualTo(productDto);
    }

    @Test
    void createProduct_whenNameIsMissing_thenReturn400() {
        final var URI = "/users/" + user.getId() + "/products";
        final var productDto = ProductSample.bananaWithIdDto();
        productDto.setName(null);

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createProduct_whenDescriptionIsNull_thenReturn400() {
        final var URI = "/users/" + user.getId() + "/products";
        final var productDto = ProductSample.bananaWithoutIdDto();
        productDto.setDescription(null);

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void findUserMeals_whenUserHasNoMeals_thenReturnPageSupportWithoutElements() {
        final var URI = "/users/" + user.getId() + "/meals";
        mealService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Page.class)
                .isEqualTo(new Page<>(new ArrayList<>(), 0, 10, 0));
    }

    @Test
    void findUserMeals_whenUserHasMeals_thenReturnArray() throws JsonProcessingException {
        final var URI = "/users/" + user.getId() + "/meals";
        final var meals = new ArrayList<MealDto>(3);
        meals.add(userFacade.createMeal(user.getId(), dumplingsWithoutIdDto()).block());
        meals.add(userFacade.createMeal(user.getId(), dumplingsWithoutIdDto()).block());
        meals.add(mealDto);

        final var expected = new Page<>(meals, 0, 10, meals.size());

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void createMeal_whenAllRequiredField_thenSaveMealAndReturnDto() {
        final var URI = "/users/" + user.getId() + "/meals";
        final var mealDto = dumplingsWithIdDto();
        mealDto.setUserId(user.getId());

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MealDto.class)
                .isEqualTo(mealDto);
    }

    @Test
    void createMeal_whenNameIsMissing_thenReturn400() {
        final var URI = "/users/" + user.getId() + "/meals";
        final var mealDto = dumplingsWithoutIdDto();
        mealDto.setName(null);

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createMeal_whenDescriptionIsNull_thenReturn400() {
        final var URI = "/users/" + user.getId() + "/meals";
        final var mealDto = dumplingsWithoutIdDto();
        mealDto.setDescription(null);

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Create a new meal, when recipe is missing, then return Bad Request status")
    void createMeal_whenRecipeIsNull_thenReturn400() {
        final var URI = "/users/" + user.getId() + "/meals";
        final var mealDto = dumplingsWithoutIdDto();
        mealDto.setRecipe(null);

        webTestClient.post().uri(URI)
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    private void createUser() {
        user = UserSample.johnWithoutId();
        user = userService.save(user).block();
        userDto = userDtoConverter.toDto(user);
    }

    private void createMeal() {
        providePrincipal();
        meal = MealSample.dumplingsWithoutId();
        mealDto = userFacade.createMeal(user.getId(), MealSample.dumplingsWithoutIdDto()).block();
        meal.setId(mealDto.getId());
    }

    private void createCart() {
        cart = CartSample.cart1();
        cart.setUserId(user.getId());
        cartDto = userFacade.findDtoCart(user.getId(), cart.getDate()).block();
        cart.setId(cartDto.getId());
        cart.setUserId(cartDto.getUserId());
    }

    private void providePrincipal() {
        var testingAuthentication = new TestingAuthenticationToken(user.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);
    }
}