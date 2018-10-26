package com.piotrek.diet.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.*;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.MealSample.dumplingsWithIdDto;
import static com.piotrek.diet.helpers.MealSample.dumplingsWithoutIdDto;
import static org.junit.jupiter.api.Assertions.*;
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
    private MealDtoConverter mealDtoConverter;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private MealService mealService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

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
        productService.deleteAll().block();
        mealService.deleteAll().block();
        cartService.deleteAll().block();
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
        productService.deleteAll().block();
        mealService.deleteAll().block();
        cartService.deleteAll().block();
    }


    @Test
    @DisplayName("Check if user has meal in favourites, if he has, then return true")
    void isMealFavourite_whenUserHasAMealInFavourites_thenReturnTrue() {
        final var URI = "/users/" + user.getId() + "/meals/" + meal.getId() + "/favourites";

        user.getFavouriteMeals().add(meal);
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
        mealDto.setUserId(user.getId());
        meal.setUserId(user.getId());
        list.add(mealDto);

        user.getFavouriteMeals().add(meal);
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

    @Test
    @DisplayName("Get or create cart, when found cart, then return it")
    void getOrCreateCart_whenFoundCart_thenReturnIt() {
        cart.setDate(LocalDate.of(1995, Month.MARCH, 3));
        cartDto = cartDtoConverter.toDto(cartService.save(cart).block());

        final var URI = "/users/" + user.getId() + "/carts?date=" + cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class).isEqualTo(cartDto);
    }

    @Test
    @DisplayName("Get or create cart, when not found cart, then throw NotFoundException")
    void getOrCreateCart_whenNotFoundCart_thenCreateAndReturn() {
        final var URI = "/users/" + user.getId() + "/carts?date=04-03-1995";

        cartDto.setDate(LocalDate.of(1995, Month.MARCH, 4));

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound();
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

        meal.setUserId(user.getId());
        mealDto.setUserId(user.getId());

        list.add(mealDto);

        user.getFavouriteMeals().add(meal);
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

    @Test
    @DisplayName("Add meal to cart, when cart is empty, then cart has one meal")
    void addMealToCart_whenCartIsEmpty_thenCartHasOneMeal() throws JsonProcessingException {
        var mealToAdd = MealSample.coffeeWithoutId();
        mealToAdd.getProducts().add(ProductSample.breadWithId());
        mealToAdd = mealService.save(mealToAdd).block();

        cartDto.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        cartDto.getAllProducts().addAll(mealDtoConverter.toDto(mealToAdd).getProducts());

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";

        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cartDto.getId(), responseBody.getId()),
                () -> assertEquals(cartDto.getUserId(), responseBody.getUserId()),
                () -> assertEquals(cartDto.getMeals(), responseBody.getMeals()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(cartDto.getProducts(), responseBody.getProducts()),
                () -> assertEquals(cartDto.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cartDto.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add meal to cart, when cart has one meal, then cart has two meal")
    void addMealToCart_whenCartHasOneMeal_thenCartHasTwoMeals() {
        var firstProductInMealToAdd = ProductSample.breadWithId();
        firstProductInMealToAdd.setAmount(50);

        var secondProductInMealToAdd = ProductSample.bananaWithId();
        secondProductInMealToAdd.setAmount(100);

        var mealToAdd = MealSample.dumplingsWithId();
        mealToAdd.getProducts().add(firstProductInMealToAdd);
        mealToAdd.getProducts().add(secondProductInMealToAdd);
        mealToAdd = mealService.save(mealToAdd).block();

        var productInMealInCart = ProductSample.bananaWithId();
        productInMealInCart.setAmount(60);

        var mealInCart = MealSample.coffeeWithId();
        mealInCart.getProducts().add(productInMealInCart);

        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        cartDto.getMeals().add(mealDtoConverter.toDto(mealInCart));
        cartDto.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());
        var duplicatedProduct = ProductSample.bananaWithIdDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        cartDto.getAllProducts().add(duplicatedProduct);

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cartDto.getId(), responseBody.getId()),
                () -> assertEquals(cartDto.getUserId(), responseBody.getUserId()),
                () -> assertEquals(cartDto.getDate(), responseBody.getDate()),
                () -> assertEquals(2, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(2, responseBody.getAllProducts().size())
        );
    }

    @Test
    @DisplayName("Add meal to cart, when cart has one product, then cart has one meal and one product")
    void addMealToCart_whenCartHasOneProduct_thenCartHasOneMealAndOneProduct() {
        var mealToAdd = MealSample.dumplingsWithId();
        mealToAdd.getProducts().add(ProductSample.breadWithId());
        mealToAdd.getProducts().add(ProductSample.bananaWithId());
        mealToAdd = mealService.save(mealToAdd).block();

        var productInCart = ProductSample.bananaWithId();

        cart.getProducts().add(productInCart);
        cart = cartService.save(cart).block();

        cartDto.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());
        var duplicatedProduct = ProductSample.bananaWithIdDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        cartDto.getAllProducts().add(duplicatedProduct);

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cartDto.getId(), responseBody.getId()),
                () -> assertEquals(cartDto.getUserId(), responseBody.getUserId()),
                () -> assertEquals(cartDto.getMeals(), responseBody.getMeals()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(cartDto.getProducts(), responseBody.getProducts()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(cartDto.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(2, responseBody.getAllProducts().size()),
                () -> assertEquals(cartDto.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart has one product")
    void addProductToCart_whenCartIsEmpty_thenCartHasOneProduct() {
        var productToAdd = productService.save(ProductSample.bananaWithId()).block();

        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getAllProducts().add(ProductSample.bananaWithIdDto());

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cartDto.getId(), responseBody.getId()),
                () -> assertEquals(cartDto.getUserId(), responseBody.getUserId()),
                () -> assertEquals(cartDto.getMeals(), responseBody.getMeals()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(cartDto.getProducts(), responseBody.getProducts()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(cartDto.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cartDto.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add product to cart, when cart has one product, then cart has two products")
    void addProductToCart_whenCartHasOneProduct_thenCartHasTwoProducts() {
        var productToAdd = productService.save(ProductSample.bananaWithId()).block();
        var productInCart = productService.save(ProductSample.breadWithId()).block();

        cart.getProducts().add(productInCart);
        cart = cartService.save(cart).block();


        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getAllProducts().add(ProductSample.breadWithIdDto());
        cartDto.getAllProducts().add(ProductSample.bananaWithIdDto());


        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cartDto.getId(), responseBody.getId()),
                () -> assertEquals(cartDto.getUserId(), responseBody.getUserId()),
                () -> assertEquals(cartDto.getMeals(), responseBody.getMeals()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(cartDto.getProducts(), responseBody.getProducts()),
                () -> assertEquals(2, responseBody.getProducts().size()),
                () -> assertEquals(cartDto.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(2, responseBody.getAllProducts().size()),
                () -> assertEquals(cartDto.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Add product to cart, when cart has one meal, then cart has one meal and one product")
    void addProductToCart_whenCartHasOneMeal_thenCartHasOneMealAndOneProduct() {
        var productToAdd = productService.save(ProductSample.bananaWithId()).block();

        var mealInCart = MealSample.coffeeWithId();
        mealInCart.getProducts().add(ProductSample.bananaWithId());

        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        cartDto.getMeals().add(mealDtoConverter.toDto(mealInCart));
        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        var duplicatedProduct = ProductSample.bananaWithIdDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        cartDto.getAllProducts().add(duplicatedProduct);

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.put().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cartDto.getId(), responseBody.getId()),
                () -> assertEquals(cartDto.getUserId(), responseBody.getUserId()),
                () -> assertEquals(cartDto.getMeals(), responseBody.getMeals()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(cartDto.getProducts(), responseBody.getProducts()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(cartDto.getAllProducts(), responseBody.getAllProducts()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cartDto.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal from cart, when cart has one meal, then cart is empty")
    void deleteMealFromCart_whenCartHasOneMeal_thenCartIsEmpty() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        cart.getMeals().add(mealToDelete);
        cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal from cart, when cart has two meals, then cart has one meal")
    void deleteMealFromCart_whenCartHasTwoMeals_thenCartHasOneMeal() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        var mealInCart = MealSample.dumplingsWithId();
        mealInCart.getProducts().add(ProductSample.breadWithId());
        mealInCart = mealService.save(mealInCart).block();

        cart.getMeals().add(mealToDelete);
        cart.getMeals().add(mealInCart);
        cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal form cart, when cart has one product and one meal, then cart has one product")
    void deleteMealFromCart_whenCartHasOneProductAndOneMeal_thenCartHasOneProduct() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();

        var productInCart = productService.save(ProductSample.breadWithId()).block();

        cart.getMeals().add(mealToDelete);
        cart.getProducts().add(productInCart);
        cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete meal from cart, when cart is empty, then cart is still empty")
    void deleteMealFromCart_whenCartIsEmpty_thenCartIsStillEmpty() {
        var mealToDelete = MealSample.coffeeWithId();
        mealToDelete.getProducts().add(ProductSample.bananaWithId());
        mealToDelete = mealService.save(mealToDelete).block();


        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product from cart, when cart has one product, then cart is empty")
    void deleteProductFromCart_whenCartHasOneProduct_thenCartIsEmpty() {
        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        cart.getProducts().add(productToDelete);
        cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product from cart, when cart has two products, then cart has one product")
    void deleteProductFromCart_whenCartHasTwoProducts_thenCartHasOneProduct() {
        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        var productInCart = productService.save(ProductSample.bananaWithId()).block();

        cart.getProducts().add(productToDelete);
        cart.getProducts().add(productInCart);
        cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(1, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product form cart, when cart has one product and one meal, then cart has one meal")
    void deleteProductFromCart_whenCartHasOneProductAndOneMeal_thenCartHasOneMeal() {
        var mealInCart = MealSample.coffeeWithId();
        mealInCart.getProducts().add(ProductSample.bananaWithId());
        mealInCart = mealService.save(mealInCart).block();

        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        cart.getMeals().add(mealInCart);
        cart.getProducts().add(productToDelete);
        cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(1, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(1, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
    }

    @Test
    @DisplayName("Delete product from cart, when cart is empty, then cart is still empty")
    void deleteProductFromCart_whenCartIsEmpty_thenCartIsStillEmpty() {
        var productToDelete = productService.save(ProductSample.breadWithId()).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertAll(
                () -> assertEquals(cart.getId(), responseBody.getId()),
                () -> assertEquals(cart.getUserId(), responseBody.getUserId()),
                () -> assertEquals(0, responseBody.getMeals().size()),
                () -> assertEquals(0, responseBody.getProducts().size()),
                () -> assertEquals(0, responseBody.getAllProducts().size()),
                () -> assertEquals(cart.getDate(), responseBody.getDate())
        );
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
        cartDto = cartDtoConverter.toDto(cartService.save(cart).block());
        cart.setId(cartDto.getId());
        cart.setUserId(cartDto.getUserId());
    }

    private void providePrincipal() {
        var testingAuthentication = new TestingAuthenticationToken(user.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);
    }
}