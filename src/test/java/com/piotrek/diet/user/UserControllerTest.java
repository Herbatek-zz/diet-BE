package com.piotrek.diet.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.PageSupport;
import com.piotrek.diet.helpers.config.DataBaseConfigIntegrationTests;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealFacade;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductFacade;
import com.piotrek.diet.sample.ProductSample;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;

import static com.piotrek.diet.sample.MealSample.dumplingsWithIdDto;
import static com.piotrek.diet.sample.MealSample.dumplingsWithoutIdDto;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseConfigIntegrationTests.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDtoConverter userDtoConverter;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private MealFacade mealFacade;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user1;
    private UserDto userDto1;

    @BeforeEach
    void setUp() {
        userService.deleteAll().block();
        createUsers();
        webTestClient = WebTestClient
                .bindToController(new UserController(userService, userDtoConverter, productFacade, mealFacade))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        userService.deleteAll().block();
    }

    @Test
    void findUserById_whenUserExists_thenReturnUserDto() {
        webTestClient.get().uri("/users/" + user1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(UserDto.class)
                .isEqualTo(userDto1);
    }

    @Test
    void findUserById_whenUserDoesNotExist_thenReturn404() {
        var invalidId = "iDoesNotExist";

        webTestClient.get().uri("/users/" + invalidId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findUserProducts_whenUserHasNoProducts_thenReturnPageSupportWithoutElements() {
        webTestClient.get().uri("/users/" + user1.getId() + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageSupport.class)
                .isEqualTo(new PageSupport<>(new ArrayList<>(), 0, 10, 0));
    }

    @Test
    void findUserProducts_whenUserHasProducts_thenReturnArray() throws JsonProcessingException {

        // this need be added because below we create products using facade
        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        var products = new ArrayList<ProductDto>(2);
        products.add(productFacade.createProduct(user1.getId(), ProductSample.bananaWithoutIdDto()).block());
        products.add(productFacade.createProduct(user1.getId(), ProductSample.breadWithoutIdDto()).block());


        var expected = new PageSupport<>(products, 0, 10, products.size());

        webTestClient.get().uri("/users/" + user1.getId() + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void createProduct_whenAllRequiredField_thenSaveProductAndReturnDto() {
        var productDto = ProductSample.bananaWithIdDto();
        productDto.setUserId(user1.getId());

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/products")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .isEqualTo(productDto);
    }

    @Test
    void createProduct_whenNameIsMissing_thenReturn400() {
        var productDto = ProductSample.bananaWithIdDto();
        productDto.setName(null);

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/products")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createProduct_whenDescriptionIsNull_thenReturn400() {
        ProductDto productDto = ProductSample.bananaWithIdDto();
        productDto.setDescription(null);

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/products")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(productDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void findUserMeals_whenUserHasNoMeals_thenReturnPageSupportWithoutElements() {
        webTestClient.get().uri("/users/" + user1.getId() + "/meals")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageSupport.class)
                .isEqualTo(new PageSupport<>(new ArrayList<>(), 0, 10, 0));
    }

    @Test
    void findUserMeals_whenUserHasMeals_thenReturnArray() throws JsonProcessingException {

        // this need be added because below we create products using facade
        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        var meals = new ArrayList<MealDto>(2);
        meals.add(mealFacade.createMeal(user1.getId(), dumplingsWithoutIdDto()).block());
        meals.add(mealFacade.createMeal(user1.getId(), dumplingsWithoutIdDto()).block());


        var expected = new PageSupport<>(meals, 0, 10, meals.size());

        webTestClient.get().uri("/users/" + user1.getId() + "/meals")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void createMeal_whenAllRequiredField_thenSaveMealAndReturnDto() {
        var mealDto = dumplingsWithIdDto();
        mealDto.setUserId(user1.getId());

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/meals")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MealDto.class)
                .isEqualTo(mealDto);
    }

    @Test
    void createMeal_whenNameIsMissing_thenReturn400() {
        var mealDto = dumplingsWithIdDto();
        mealDto.setName(null);

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/meals")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createMeal_whenDescriptionIsNull_thenReturn400() {
        var mealDto = dumplingsWithIdDto();
        mealDto.setDescription(null);

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/meals")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createMeal_whenRecipeIsNull_thenReturn400() {
        var mealDto = dumplingsWithIdDto();
        mealDto.setRecipe(null);

        var testingAuthentication = new TestingAuthenticationToken(user1.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.post().uri("/users/" + user1.getId() + "/meals")
                .contentType(APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(mealDto))
                .exchange()
                .expectStatus().isBadRequest();
    }

    private void createUsers() {
        user1 = UserSample.baileyWithoutId();

        user1 = userService.save(user1).block();

        userDto1 = userDtoConverter.toDto(user1);
    }
}