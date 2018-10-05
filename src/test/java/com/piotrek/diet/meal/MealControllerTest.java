package com.piotrek.diet.meal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.sample.ProductSample;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealControllerTest {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealFacade mealFacade;

    @Autowired
    private MealDtoConverter mealDtoConverter;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Meal meal1;
    private Meal meal2;
    private MealDto mealDto1;
    private MealDto mealDto2;

    private final String INCORRECT_ID = "badIDXD";

    @BeforeEach
    void setUp() {
        mealService.deleteAll().block();
        createMeals();
        webTestClient = WebTestClient
                .bindToController(new MealController(mealService, mealFacade, mealDtoConverter))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        mealService.deleteAll().block();
    }

    @Test
    void findById_whenFound_thenReturnMeal() {
        webTestClient.get().uri("/meals/" + meal1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(MealDto.class)
                .isEqualTo(mealDto1);
    }

    @Test
    void findById_whenNotFound_thenThrowNotFoundException() {
        webTestClient.get().uri("/meals/" + INCORRECT_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8);
    }

    @Test
    void searchByName_when2MealsAndQueryOneOfThem_thenReturnPageableWithOneMeal() throws JsonProcessingException {
        var expected = new Page<>(List.of(mealDtoConverter.toDto(meal1)), 0, 10, 1);

        webTestClient.get().uri("/meals/search?query=" + meal1.getName())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2MealsAndNoQuery_thenReturnPageableWithAllMeals() throws JsonProcessingException {
        var products = new ArrayList<Meal>();
        products.add(meal1);
        products.add(meal2);
        var productsDto = mealDtoConverter.listToDto(products);
        var expected = new Page<>(productsDto, 0, 10, products.size());

        webTestClient.get().uri("/meals/search")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_whenNoMealsAndNoQuery_thenReturnEmptyPageable() throws JsonProcessingException {

        var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        mealService.deleteAll().block();

        webTestClient.get().uri("/meals/search")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2MealsAndWrongQuery_thenReturnEmptyPageable() throws JsonProcessingException {
        var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        webTestClient.get().uri("/meals/search?query=lol123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2MealsAndQueryWithCapitalCharacters_thenReturnMatchMeal() throws JsonProcessingException {
        var list = new ArrayList<MealDto>(1);
        list.add(mealDto1);
        var expected = new Page<>(list, 0, 10, 1);

        webTestClient.get().uri("/meals/search?query=" + meal1.getName().toUpperCase())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void searchByName_when2MealsAndNoQueryAndPageSize1_thenReturnFirstPageWithOneMeal() throws JsonProcessingException {
        var products = new ArrayList<Meal>();
        products.add(meal1);
        products.add(meal2);
        var expected = new Page<>(List.of(meal1), 0, 1, products.size());

        webTestClient.get().uri("/meals/search?size=1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void findAll_whenDefaultParamsTotalElements0_thenReturnPageSupportWithoutContent() throws JsonProcessingException {
        var expected = new Page<MealDto>(new ArrayList<>(), 0, 10, 0);

        mealService.deleteAll().block();

        webTestClient.get().uri("/meals")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void findAll_whenDefaultParamsTotalElements2_thenReturnPageSupportWith2Meals() throws JsonProcessingException {
        var meals = new ArrayList<Meal>();
        meals.add(meal1);
        meals.add(meal2);
        var mealsDto = mealDtoConverter.listToDto(meals);
        var expected = new Page<>(mealsDto, 0, 10, meals.size());

        webTestClient.get().uri("/meals")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void findAll_whenPageNumber1PageSize1TotalElements2_returnSecondPageWithOneMeal() throws JsonProcessingException {
        var meals = new ArrayList<Meal>();
        meals.add(meal1);
        meals.add(meal2);
        var mealDtos = mealDtoConverter.listToDto(meals);
        var expected = new Page<>(mealDtos
                .stream()
                .skip(1)
                .limit(1)
                .collect(Collectors.toList()), 1, 1, meals.size());

        webTestClient.get().uri("/meals?page=1&size=1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    void deleteById() {
        webTestClient.delete().uri("/meals/" + meal1.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void addProducts() throws JsonProcessingException {
        var productDtos = new ArrayList<ProductDto>(2);
        productDtos.add(ProductSample.bananaWithIdDto());
        productDtos.add(ProductSample.breadWithIdDto());

        var expected = dumplingsWithIdDto();
        expected.setProducts(productDtos);

        expected.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        expected.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        expected.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        expected.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        expected.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        expected.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        expected.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());

        var testingAuthentication = new TestingAuthenticationToken(meal1.getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.put().uri("/meals/" + meal1.getId())
                .body(BodyInserters.fromObject(productDtos))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    private void createMeals() {
        meal1 = dumplingsWithId();
        meal2 = coffeeWithId();

        meal1 = mealService.save(meal1).block();
        meal2 = mealService.save(meal2).block();

        mealDto1 = mealDtoConverter.toDto(meal1);
        mealDto2 = mealDtoConverter.toDto(meal2);
    }
}