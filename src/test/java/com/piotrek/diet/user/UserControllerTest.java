package com.piotrek.diet.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.cart.*;
import com.piotrek.diet.helpers.*;
import com.piotrek.diet.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductService;
import org.decimal4j.util.DoubleRounder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertCartFields;
import static com.piotrek.diet.helpers.AssertEqualAllFields.assertProductFields;
import static com.piotrek.diet.helpers.MealSample.dumplingsDto;
import static com.piotrek.diet.helpers.MealSample.dumplingsWithoutIdDto;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDtoConverter userDtoConverter;

    @Autowired
    private MealDtoConverter mealDtoConverter;

    @Autowired
    private CartDtoConverter cartDtoConverter;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private CartFacade cartFacade;

    @Autowired
    private MealService mealService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private DoubleRounder doubleRounder;

    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

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
        PrincipalProvider.provide(user.getId());
        createMeal();
        createCart();
        webTestClient = WebTestClient
                .bindToController(new UserController(userFacade, cartFacade))
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
        webTestClient.mutate().filter(basicAuthentication()).build()
                .get().uri(URI)
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
    @DisplayName("Find user cart, when found cart, then return it")
    void findUserCart_whenFoundCart_thenReturnIt() {
        cart.setDate(LocalDate.of(1995, Month.MARCH, 3));
        cart = cartService.save(cart).block();

        final var URI = "/users/" + user.getId() + "/carts?date=" + cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class).isEqualTo(cartDto);
    }

    @Test
    @DisplayName("Find user cart, when not found cart, then throw NotFoundException")
    void findUserCart_whenNotFoundCart_thenCreateAndReturn() {
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
        final var productDto = ProductSample.bananaDto();
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
        final var productDto = ProductSample.bananaDto();
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
        final var mealDto = dumplingsDto();
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
    void addMealToCart_whenCartIsEmpty_thenCartHasOneMeal() {
        var mealToAdd = MealSample.coffeeWithoutId();
        mealToAdd.getProducts().add(ProductSample.bread());
        mealToAdd = mealService.save(mealToAdd).block();

        cartDto.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        cartDto.getAllProducts().addAll(mealDtoConverter.toDto(mealToAdd).getProducts());
        cartDto.setItemCounter(1);

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";

        CartDto responseBody = webTestClient.post().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Add meal to cart, when cart has one meal, then cart has two meal")
    void addMealToCart_whenCartHasOneMeal_thenCartHasTwoMeals() {
        var firstProductInMealToAdd = ProductSample.bread();
        firstProductInMealToAdd.setAmount(50);

        var secondProductInMealToAdd = ProductSample.banana();

        var mealToAdd = MealSample.dumplings();
        mealToAdd.getProducts().add(firstProductInMealToAdd);
        mealToAdd.getProducts().add(secondProductInMealToAdd);
        mealToAdd.setFat(mealToAdd.getProducts().get(0).getFat() + mealToAdd.getProducts().get(1).getFat());
        mealToAdd.setProtein(mealToAdd.getProducts().get(0).getProtein() + mealToAdd.getProducts().get(1).getProtein());
        mealToAdd.setCarbohydrate(mealToAdd.getProducts().get(0).getCarbohydrate() + mealToAdd.getProducts().get(1).getCarbohydrate());
        mealToAdd.setKcal(mealToAdd.getProducts().get(0).getKcal() + mealToAdd.getProducts().get(1).getKcal());
        mealToAdd = mealService.save(mealToAdd).block();

        var productInMealInCart = ProductSample.banana();
        productInMealInCart.setAmount(60);

        var mealInCart = MealSample.coffee();
        mealInCart.getProducts().add(productInMealInCart);

        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        cartDto.getMeals().add(mealDtoConverter.toDto(mealInCart));
        cartDto.getMeals().add(mealDtoConverter.toDto(mealToAdd));
        cartDto.getAllProducts().add(ProductSample.breadDto());
        cartDto.setKcal(mealToAdd.getKcal() + productInMealInCart.getKcal());
        cartDto.setFat(mealToAdd.getFat() + productInMealInCart.getFat());
        cartDto.setProtein(mealToAdd.getProtein() + productInMealInCart.getProtein());
        cartDto.setCarbohydrate(mealToAdd.getCarbohydrate() + productInMealInCart.getCarbohydrate());

        var duplicatedProduct = ProductSample.bananaDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        cartDto.getAllProducts().add(duplicatedProduct);
        cartDto.setItemCounter(2);

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.post().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Add meal to cart, when cart has one product, then cart has one meal and one product")
    void addMealToCart_whenCartHasOneProduct_thenCartHasOneMealAndOneProduct() {
        var mealToAdd = MealSample.dumplings();
        mealToAdd.getProducts().add(ProductSample.bread());
        mealToAdd.getProducts().add(ProductSample.banana());
        mealToAdd = mealService.save(mealToAdd).block();
        var mealDtoInCart = mealDtoConverter.toDto(mealToAdd);

        var productInCart = ProductSample.banana();

        cart.getProducts().add(productInCart);
        cart = cartService.save(cart).block();

        var duplicatedProduct = ProductSample.bananaDto();
        duplicatedProduct.setAmount(duplicatedProduct.getAmount() * 2);
        cartDto.getProducts().add(ProductSample.bananaDto());
        cartDto.getAllProducts().add(ProductSample.breadDto());
        cartDto.getAllProducts().add(duplicatedProduct);
        cartDto.getMeals().add(mealDtoInCart);
        cartDto.setItemCounter(2);

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.post().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Add product to cart, when cart is empty, then cart has one product")
    void addProductToCart_whenCartIsEmpty_thenCartHasOneProduct() {
        var productToAdd = productService.save(ProductSample.banana()).block();

        cartDto.getProducts().add(ProductSample.bananaDto());
        cartDto.getAllProducts().add(ProductSample.bananaDto());
        cartDto.setItemCounter(1);

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";


        CartDto responseBody = webTestClient.post().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Add product to cart, when cart has one product, then cart has two products")
    void addProductToCart_whenCartHasOneProduct_thenCartHasTwoProducts() {
        final var productAlreadyInCart = ProductSample.banana();
        cart.getProducts().add(productAlreadyInCart);
        cart = cartService.save(cart).block();

        final var productToAdd = productService.save(ProductSample.bread()).block();

        cartDto = cartDtoConverter.toDto(cart);
        cartDto.getProducts().add(productToAdd);
        cartDto.getAllProducts().add(productToAdd);
        cartDto.setItemCounter(cartDto.getItemCounter() + 1);
        cartDto.setKcal(doubleRounder.round(cartDto.getKcal() + productToAdd.getKcal()));
        cartDto.setProtein(doubleRounder.round(cartDto.getProtein() + productToAdd.getProtein()));
        cartDto.setCarbohydrate(doubleRounder.round(cartDto.getCarbohydrate() + productToAdd.getCarbohydrate()));
        cartDto.setFat(doubleRounder.round(cartDto.getFat() + productToAdd.getFat()));


        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=100";

        CartDto responseBody = webTestClient.post().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Add product to cart, when cart has one meal, then cart has one meal and one product")
    void addProductToCart_whenCartHasOneMeal_thenCartHasOneMealAndOneProduct() {
        var productToAdd = productService.save(ProductSample.banana()).block();
        productToAdd.setAmount(70);
        productToAdd.setProtein(doubleRounder.round(productToAdd.getProtein() * 0.7));
        productToAdd.setCarbohydrate(doubleRounder.round(productToAdd.getCarbohydrate() * 0.7));
        productToAdd.setFat(doubleRounder.round(productToAdd.getFat() * 0.7));
        productToAdd.setFibre(doubleRounder.round(productToAdd.getFibre() * 0.7));
        productToAdd.setKcal(doubleRounder.round(productToAdd.getKcal() * 0.7));
        productToAdd.setCarbohydrateExchange(doubleRounder.round(productToAdd.getCarbohydrateExchange() * 0.7));
        productToAdd.setProteinAndFatEquivalent(doubleRounder.round(productToAdd.getProteinAndFatEquivalent() * 0.7));

        var mealInCart = MealSample.coffee();
        mealInCart.getProducts().add(ProductSample.banana());
        var mealDtoInCart = mealDtoConverter.toDto(mealInCart);

        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        var duplicatedProduct = ProductSample.bananaDto();
        duplicatedProduct.setAmount(productToAdd.getAmount() + mealInCart.getProducts().get(0).getAmount());
        duplicatedProduct.setKcal(productToAdd.getKcal() + mealInCart.getProducts().get(0).getKcal());
        duplicatedProduct.setProtein(productToAdd.getProtein() + mealInCart.getProducts().get(0).getProtein());
        duplicatedProduct.setFat(productToAdd.getFat() + mealInCart.getProducts().get(0).getFat());
        duplicatedProduct.setCarbohydrate(productToAdd.getCarbohydrate() + mealInCart.getProducts().get(0).getCarbohydrate());

        cartDto.getMeals().add(mealDtoInCart);
        cartDto.getProducts().add(productToAdd);
        cartDto.getAllProducts().add(duplicatedProduct);
        cartDto.setProtein(duplicatedProduct.getProtein());
        cartDto.setCarbohydrate(duplicatedProduct.getCarbohydrate());
        cartDto.setFat(duplicatedProduct.getFat());
        cartDto.setKcal(duplicatedProduct.getKcal());
        cartDto.setItemCounter(2);

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToAdd.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "&amount=70";


        CartDto responseBody = webTestClient.post().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
        assertProductFields(cartDto.getProducts().get(0), responseBody.getProducts().get(0));
    }

    @Test
    @DisplayName("Delete meal from cart, when cart has one meal, then cart is empty")
    void deleteMealFromCart_whenCartHasOneMeal_thenCartIsEmpty() {
        var mealToDelete = MealSample.coffee();
        mealToDelete.getProducts().add(ProductSample.banana());
        mealToDelete = mealService.save(mealToDelete).block();

        cart.getMeals().add(mealToDelete);
        cart = cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart has two meals, then cart has one meal")
    void deleteMealFromCart_whenCartHasTwoMeals_thenCartHasOneMeal() {
        var mealToDelete = MealSample.coffee();
        mealToDelete.getProducts().add(ProductSample.banana());
        mealToDelete = mealService.save(mealToDelete).block();

        var mealInCart = MealSample.dumplings();
        mealInCart.getProducts().add(ProductSample.bread());
        mealInCart = mealService.save(mealInCart).block();
        var mealDtoInCart = mealDtoConverter.toDto(mealInCart);

        cart.getMeals().add(mealToDelete);
        cart.getMeals().add(mealInCart);
        cart = cartService.save(cart).block();

        cartDto.getAllProducts().add(mealDtoInCart.getProducts().get(0));
        cartDto.getMeals().add(mealDtoInCart);
        cartDto.setItemCounter(1);
        cartDto.setKcal(mealDtoInCart.getProducts().get(0).getKcal());
        cartDto.setProtein(mealDtoInCart.getProducts().get(0).getProtein());
        cartDto.setCarbohydrate(mealDtoInCart.getProducts().get(0).getCarbohydrate());
        cartDto.setFat(mealDtoInCart.getProducts().get(0).getFat());

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete meal form cart, when cart has one product and one meal, then cart has one product")
    void deleteMealFromCart_whenCartHasOneProductAndOneMeal_thenCartHasOneProduct() {
        var mealToDelete = MealSample.coffee();
        mealToDelete.getProducts().add(ProductSample.banana());
        mealToDelete = mealService.save(mealToDelete).block();

        productService.save(ProductSample.bread()).block();

        cart.getMeals().add(mealToDelete);
        cart.getProducts().add(ProductSample.bread());
        cart = cartService.save(cart).block();
        cart.getMeals().remove(0);
        cartDto = cartDtoConverter.toDto(cart);

        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete meal from cart, when cart is empty, then cart is still empty")
    void deleteMealFromCart_whenCartIsEmpty_thenCartIsStillEmpty() {
        var mealToDelete = MealSample.coffee();
        mealToDelete.getProducts().add(ProductSample.banana());
        mealToDelete = mealService.save(mealToDelete).block();


        final var URI = "/users/" + cart.getUserId() + "/carts/meals/" + mealToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete product from cart, when cart has one product, then cart is empty")
    void deleteProductFromCart_whenCartHasOneProduct_thenCartIsEmpty() {
        var productToDelete = productService.save(ProductSample.bread()).block();

        cart.getProducts().add(ProductSample.bread());
        cart = cartService.save(cart).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete product from cart, when cart has two products, then cart has one product")
    void deleteProductFromCart_whenCartHasTwoProducts_thenCartHasOneProduct() {
        var productToDelete = productService.save(ProductSample.bread()).block();
        var productInCart = productService.save(ProductSample.banana()).block();

        cart.getProducts().add(ProductSample.bread());
        cart.getProducts().add(ProductSample.banana());
        cart = cartService.save(cart).block();

        cartDto.getProducts().add(productInCart);
        cartDto.getAllProducts().add(productInCart);
        cartDto.setItemCounter(1);
        cartDto.setProtein(productInCart.getProtein());
        cartDto.setCarbohydrate(productInCart.getCarbohydrate());
        cartDto.setFat(productInCart.getFat());
        cartDto.setKcal(productInCart.getKcal());

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete product form cart, when cart has one product and one meal, then cart has one meal")
    void deleteProductFromCart_whenCartHasOneProductAndOneMeal_thenCartHasOneMeal() {
        var mealInCart = MealSample.coffee();
        mealInCart.getProducts().add(ProductSample.banana());
        mealInCart = mealService.save(mealInCart).block();
        var mealDtoInCart = mealDtoConverter.toDto(mealInCart);

        var productToDelete = productService.save(ProductSample.bread()).block();
        var productDtoToDelete = ProductSample.bread();

        cart.getMeals().add(mealInCart);
        cart.getProducts().add(productDtoToDelete);
        cart = cartService.save(cart).block();

        cartDto.getMeals().add(mealDtoInCart);
        cartDto.getAllProducts().add(mealDtoInCart.getProducts().get(0));
        cartDto.setItemCounter(1);
        cartDto.setKcal(mealDtoInCart.getProducts().get(0).getKcal());
        cartDto.setProtein(mealDtoInCart.getProducts().get(0).getProtein());
        cartDto.setCarbohydrate(mealDtoInCart.getProducts().get(0).getCarbohydrate());
        cartDto.setFat(mealDtoInCart.getProducts().get(0).getFat());

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    @Test
    @DisplayName("Delete product from cart, when cart is empty, then cart is still empty")
    void deleteProductFromCart_whenCartIsEmpty_thenCartIsStillEmpty() {
        var productToDelete = productService.save(ProductSample.bread()).block();

        final var URI = "/users/" + cart.getUserId() + "/carts/products/" + productToDelete.getId() + "?date=" +
                cart.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        CartDto responseBody = webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartDto.class)
                .returnResult()
                .getResponseBody();

        assertCartFields(cartDto, responseBody);
    }

    private void createUser() {
        user = UserSample.john();
        user = userService.save(user).block();
        userDto = userDtoConverter.toDto(user);
    }

    private void createMeal() {
        meal = MealSample.dumplingsWithoutId();
        mealDto = userFacade.createMeal(user.getId(), MealSample.dumplingsWithoutIdDto()).block();
        meal.setId(mealDto.getId());
    }

    private void createCart() {
        cart = CartSample.cart1();
        cart.setUserId(user.getId());
        cart = cartService.save(cart).block();
        cartDto = cartDtoConverter.toDto(cart);
    }

}