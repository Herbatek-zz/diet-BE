package com.piotrek.diet.meal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.ProductDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.piotrek.diet.sample.MealSample.*;
import static com.piotrek.diet.sample.ProductSample.bananaWithIdDto;
import static com.piotrek.diet.sample.ProductSample.breadWithIdDto;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealControllerTest {

    @Autowired
    private MealService mealService;

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

    @BeforeEach
    void setUp() {
        mealService.deleteAll().block();
        createMeals();
        webTestClient = WebTestClient
                .bindToController(new MealController(mealService))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        mealService.deleteAll().block();
    }


    @Test
    @DisplayName("Find all without any parameters and when there are no meals, then return empty page")
    void findAll_whenDefaultParamsTotalElements0_thenReturnPageWithEmptyList() throws JsonProcessingException {
        final var URI = "/meals";
        final var expected = new Page<MealDto>(new ArrayList<>(), 0, 10, 0);

        mealService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all without any parameters and there are two meals, then return page with two meals")
    void findAll_whenDefaultParamsTotalElements2_thenReturnPageSupportWith2Meals() throws JsonProcessingException {
        final var URI = "/meals";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(mealDto1, mealDto2)), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("When getAll page = 1, pageSize = 1 and there are 2 meals, then should be returned second page(because first page = 0) with one meal")
    void findAll_whenPageNumber1PageSize1TotalElements2_returnSecondPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals?page=1&size=1";
        var expected = new Page<>(new ArrayList<>(Arrays.asList(mealDto1, mealDto2))
                .stream()
                .skip(1)
                .limit(1)
                .collect(Collectors.toList()), 1, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("When findMealDtoById finds a single meal, meal will be returned as dto")
    void findById_whenFound_thenReturnMeal() {
        final var URI = "/meals/" + meal1.getId();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(MealDto.class)
                .isEqualTo(mealDto1);
    }

    @Test
    @DisplayName("When findMealDtoById doesn't find a meal, throws NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var URI = "/meals/aBadId";

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    @Test
    @DisplayName("Search meals, when there is two meals, but query matches for one of them, one of them will be returned in page wrapper")
    void searchByName_when2MealsAndQueryOneOfThem_thenReturnPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals/search?query=" + meal1.getName();
        final var expected = new Page<>(List.of(mealDtoConverter.toDto(meal1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals by name, when no query, then all products will be returned in page wrapper")
    void searchByName_when2MealsAndNoQuery_thenReturnPageWithAllMeals() throws JsonProcessingException {
        final var URI = "/meals/search";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(meal1, meal2))
                .stream()
                .map(mealDtoConverter::toDto)
                .collect(Collectors.toCollection(ArrayList::new)), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals by name, when no query and no meals, will be returned empty list wrapped in page")
    void searchByName_whenNoMealsAndNoQuery_thenReturnEmptyPage() throws JsonProcessingException {
        final var URI = "/meals/search";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        mealService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when query doesn't match any meal, then will be returned empty list wrapped in page")
    void searchByName_when2MealsAndQueryDoesNotMatch_thenReturnEmptyPage() throws JsonProcessingException {
        final var URI = "/meals/search?query=lol123";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when there are two products but query matches only one of them, then return page with one meal")
    void searchByName_when2MealsAndQueryCapitalCaseMatchOnlyOneOfThem_thenReturnMatchMealInPage() throws JsonProcessingException {
        final var URI = "/meals/search?query=" + meal1.getName().toUpperCase();
        final var expected = new Page<>(new ArrayList<>(Collections.singletonList(mealDto1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when there are 2 meals, no query and pageSize = 1, then return first page with one meal")
    void searchByName_when2MealsAndNoQueryAndPageSize1_thenReturnFirstPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals/search?size=1";
        final var expected = new Page<>(List.of(meal1), 0, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("When we add products to a meal, then the meal should be with added products")
    void updateMeal_whenUpdateMeal_thenMealShouldBeUpdated() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();
        final var productDtos = new ArrayList<ProductDto>(Arrays.asList(bananaWithIdDto(), breadWithIdDto()));

        final var update = dumplingsWithIdDto();
        update.setName("updated name");
        update.setRecipe("updated recipe");
        update.setDescription("updated description");
        update.setImageUrl("new image");
        update.setProducts(productDtos);

        update.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        update.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        update.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        update.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        update.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        update.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        update.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());

        var testingAuthentication = new TestingAuthenticationToken(meal1.getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(update));

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(update));
    }

    @Test
    @DisplayName("When update meal, and there is missing name, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoName_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setName(null);

        var testingAuthentication = new TestingAuthenticationToken(meal1.getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When update meal, and there is missing recipe, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoRecipe_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setRecipe(null);

        var testingAuthentication = new TestingAuthenticationToken(meal1.getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When update meal, and there is missing description, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoDescription_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setDescription(null);

        var testingAuthentication = new TestingAuthenticationToken(meal1.getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When update meal, and there is missing imageUrl, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoImageUrl_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();
        final var productDtos = new ArrayList<ProductDto>(Arrays.asList(bananaWithIdDto(), breadWithIdDto()));

        final var update = dumplingsWithIdDto();
        update.setImageUrl(null);
        update.setProducts(productDtos);

        update.setProtein(productDtos.get(0).getProtein() + productDtos.get(1).getProtein());
        update.setFat(productDtos.get(0).getFat() + productDtos.get(1).getFat());
        update.setCarbohydrate(productDtos.get(0).getCarbohydrate() + productDtos.get(1).getCarbohydrate());
        update.setFibre(productDtos.get(0).getFibre() + productDtos.get(1).getFibre());
        update.setKcal(productDtos.get(0).getKcal() + productDtos.get(1).getKcal());
        update.setProteinAndFatEquivalent(productDtos.get(0).getProteinAndFatEquivalent() + productDtos.get(1).getProteinAndFatEquivalent());
        update.setCarbohydrateExchange(productDtos.get(0).getCarbohydrateExchange() + productDtos.get(1).getCarbohydrateExchange());

        var testingAuthentication = new TestingAuthenticationToken(meal1.getUserId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When delete a meal, then the meal shouldn't be available in database")
    void deleteById() {
        final var URI = "/meals/" + meal1.getId();
        webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
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