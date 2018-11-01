package com.piotrek.diet.meal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.PrincipalProvider;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.ProductDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.bananaWithIdDto;
import static com.piotrek.diet.helpers.ProductSample.breadWithIdDto;
import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    private ObjectMapper objectMapper;

    private WebTestClient webTestClient;

    private Meal meal1;
    private Meal meal2;
    private MealDto mealDto1;
    private MealDto mealDto2;

    @BeforeEach
    void beforeEach() {
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
    @DisplayName("Find all meals, when no meals, then return empty page")
    void findAll_whenNoMeals_thenReturnEmptyPage() throws JsonProcessingException {
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
    @DisplayName("Find all meals, when found two meals, then return page with two meals")
    void findAll_whenFoundTwoMeals_thenReturnPageWithTwoMeals() throws JsonProcessingException {
        final var URI = "/meals";
        final var expected = new Page<>(List.of(mealDto1, mealDto2), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all meals, when found two meals, page=1, then return empty page")
    void findAll_whenFoundTwoMealsPageOne_thenReturnEmptyPage() throws JsonProcessingException {
        final var URI = "/meals?page=1";
        final var expected = new Page<>(List.of(), 1, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all, page=1, pageSize=1, when two meals, then return second page with one meal")
    void findAll_whenTwoMealsPageOnePageSizeOne_returnSecondPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals?page=1&size=1";
        final var expected = new Page<>(List.of(mealDto2), 1, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find meal by id, when found, then return")
    void findById_whenFound_thenReturn() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(mealDto1));
    }

    @Test
    @DisplayName("Find meal by id, when not found, throws NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var URI = "/meals/aBadId";

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    @Test
    @DisplayName("Search meals, when two meals and query matches one of them, then return page with one meal")
    void searchByName_whenTwoMealsAndQueryMatchesOneOfThem_thenReturnPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals/search?query=" + meal1.getName();
        final var expected = new Page<>(List.of(mealDtoConverter.toDto(meal1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals by name, when two meals and no query, then return page with all meals")
    void searchByName_whenTwoMealsAndNoQuery_thenReturnPageWithAllMeals() throws JsonProcessingException {
        final var URI = "/meals/search";
        final var expected = new Page<>(List.of(mealDto1, mealDto2), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals by name, when no query and no meals, return empty page")
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
    @DisplayName("Search meals, when two meals and query doesn't match any of them, then return empty page")
    void searchByName_whenTwoMealsAndQueryDoesNotMatchAnyOfThem_thenReturnEmptyPage() throws JsonProcessingException {
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
    void searchByName_whenTwoMealsAndQueryCapitalCaseMatchOnlyOneOfThem_thenReturnMatchMealInPage() throws JsonProcessingException {
        final var URI = "/meals/search?query=" + meal1.getName().toUpperCase();
        final var expected = new Page<>(new ArrayList<>(Collections.singletonList(mealDto1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when there are 2 meals, no query and pageSize=1, then return first page with one meal")
    void searchByName_whenTwoMealsAndNoQueryAndPageSizeOne_thenReturnFirstPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals/search?size=1";
        final var expected = new Page<>(List.of(meal1), 0, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Update meal, then meal should be updated")
    void updateMeal_whenUpdateMeal_thenMealShouldBeUpdated() {
        final var URI = "/meals/" + meal1.getId();
        PrincipalProvider.provide(meal1.getUserId());

        // prepare update
        final var productDtos = new ArrayList<ProductDto>(2);

        final var firstProductInUpdate = bananaWithIdDto();
        firstProductInUpdate.setAmount(100);
        firstProductInUpdate.setUserId("someId");
        productDtos.add(firstProductInUpdate);

        final var secondProductInUpdate = breadWithIdDto();
        secondProductInUpdate.setAmount(30);
        secondProductInUpdate.setUserId("id");
        productDtos.add(secondProductInUpdate);

        final MealDto update = dumplingsWithIdDto();
        update.setName("updated name");
        update.setRecipe("updated recipe");
        update.setDescription("updated description");
        update.setImageUrl("new image");
        update.setProducts(productDtos);


        //exchange
        MealDto responseBody = webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(MealDto.class)
                .returnResult()
                .getResponseBody();


        // prepare expected
        final MealDto expected = dumplingsWithIdDto();
        expected.setName("updated name");
        expected.setRecipe("updated recipe");
        expected.setDescription("updated description");
        expected.setImageUrl("new image");
        expected.setProducts(productDtos);
        expected.setAmount(firstProductInUpdate.getAmount() + secondProductInUpdate.getAmount());
        expected.setProtein(firstProductInUpdate.getProtein() + secondProductInUpdate.getProtein() * 0.3);
        expected.setProteinAndFatEquivalent(firstProductInUpdate.getProteinAndFatEquivalent() + secondProductInUpdate.getProteinAndFatEquivalent() * 0.3);
        expected.setFat(firstProductInUpdate.getFat() + secondProductInUpdate.getFat() * 0.3);
        expected.setFibre(firstProductInUpdate.getFibre() + secondProductInUpdate.getFibre() * 0.3);
        expected.setCarbohydrate(firstProductInUpdate.getCarbohydrate() + secondProductInUpdate.getCarbohydrate() * 0.3);
        expected.setCarbohydrateExchange(firstProductInUpdate.getCarbohydrateExchange() + secondProductInUpdate.getCarbohydrateExchange() * 0.3);
        expected.setKcal(firstProductInUpdate.getKcal() + secondProductInUpdate.getKcal() * 0.3);

        final var expectedSecondProduct = expected.getProducts().get(1);
        expectedSecondProduct.setKcal(expectedSecondProduct.getKcal() * 0.3);
        expectedSecondProduct.setProteinAndFatEquivalent(expectedSecondProduct.getProteinAndFatEquivalent() * 0.3);
        expectedSecondProduct.setProtein(expectedSecondProduct.getProtein() * 0.3);
        expectedSecondProduct.setFibre(expectedSecondProduct.getFibre() * 0.3);
        expectedSecondProduct.setFat(expectedSecondProduct.getFat() * 0.3);
        expectedSecondProduct.setCarbohydrate(expectedSecondProduct.getCarbohydrate() * 0.3);
        expectedSecondProduct.setCarbohydrateExchange(expectedSecondProduct.getCarbohydrateExchange() * 0.3);


        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(expected.getId(), responseBody.getId()),
                () -> assertEquals(expected.getName(), responseBody.getName()),
                () -> assertEquals(expected.getRecipe(), responseBody.getRecipe()),
                () -> assertEquals(expected.getDescription(), responseBody.getDescription()),
                () -> assertEquals(expected.getImageUrl(), responseBody.getImageUrl()),
                () -> assertEquals(expected.getProducts(), responseBody.getProducts()),
                () -> assertEquals(expected.getAmount(), responseBody.getAmount()),
                () -> assertEquals(expected.getProtein(), responseBody.getProtein()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), responseBody.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getCarbohydrateExchange(), responseBody.getCarbohydrateExchange()),
                () -> assertEquals(expected.getCarbohydrate(), responseBody.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), responseBody.getFat()),
                () -> assertEquals(expected.getFibre(), responseBody.getFibre()),
                () -> assertEquals(expected.getKcal(), responseBody.getKcal()),
                () -> assertEquals(expected.getUserId(), responseBody.getUserId()),
                () -> assertEquals(expected.getProducts().get(0).getId(), responseBody.getProducts().get(0).getId()),
                () -> assertEquals(expected.getProducts().get(0).getName(), responseBody.getProducts().get(0).getName()),
                () -> assertEquals(expected.getProducts().get(0).getDescription(), responseBody.getProducts().get(0).getDescription()),
                () -> assertEquals(expected.getProducts().get(0).getImageUrl(), responseBody.getProducts().get(0).getImageUrl()),
                () -> assertEquals(expected.getProducts().get(0).getAmount(), responseBody.getProducts().get(0).getAmount()),
                () -> assertEquals(expected.getProducts().get(0).getProtein(), responseBody.getProducts().get(0).getProtein()),
                () -> assertEquals(expected.getProducts().get(0).getProteinAndFatEquivalent(), responseBody.getProducts().get(0).getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getProducts().get(0).getCarbohydrateExchange(), responseBody.getProducts().get(0).getCarbohydrateExchange()),
                () -> assertEquals(expected.getProducts().get(0).getCarbohydrate(), responseBody.getProducts().get(0).getCarbohydrate()),
                () -> assertEquals(expected.getProducts().get(0).getFat(), responseBody.getProducts().get(0).getFat()),
                () -> assertEquals(expected.getProducts().get(0).getFibre(), responseBody.getProducts().get(0).getFibre()),
                () -> assertEquals(expected.getProducts().get(0).getKcal(), responseBody.getProducts().get(0).getKcal()),
                () -> assertEquals(expected.getProducts().get(0).getUserId(), responseBody.getProducts().get(0).getUserId()),
                () -> assertEquals(expected.getProducts().get(1).getId(), responseBody.getProducts().get(1).getId()),
                () -> assertEquals(expected.getProducts().get(1).getName(), responseBody.getProducts().get(1).getName()),
                () -> assertEquals(expected.getProducts().get(1).getDescription(), responseBody.getProducts().get(1).getDescription()),
                () -> assertEquals(expected.getProducts().get(1).getImageUrl(), responseBody.getProducts().get(1).getImageUrl()),
                () -> assertEquals(expected.getProducts().get(1).getAmount(), responseBody.getProducts().get(1).getAmount()),
                () -> assertEquals(expected.getProducts().get(1).getProtein(), responseBody.getProducts().get(1).getProtein()),
                () -> assertEquals(expected.getProducts().get(1).getProteinAndFatEquivalent(), responseBody.getProducts().get(1).getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getProducts().get(1).getCarbohydrateExchange(), responseBody.getProducts().get(1).getCarbohydrateExchange()),
                () -> assertEquals(expected.getProducts().get(1).getCarbohydrate(), responseBody.getProducts().get(1).getCarbohydrate()),
                () -> assertEquals(expected.getProducts().get(1).getFat(), responseBody.getProducts().get(1).getFat()),
                () -> assertEquals(expected.getProducts().get(1).getFibre(), responseBody.getProducts().get(1).getFibre()),
                () -> assertEquals(expected.getProducts().get(1).getKcal(), responseBody.getProducts().get(1).getKcal()),
                () -> assertEquals(expected.getProducts().get(1).getUserId(), responseBody.getProducts().get(1).getUserId())

        );
    }

    @Test
    @DisplayName("When update meal, and there is missing name, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoName_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setName(null);

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

        final var update = dumplingsWithIdDto();
        update.setImageUrl(null);

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
        meal1 = mealService.save(dumplingsWithId()).block();
        meal2 = mealService.save(coffeeWithId()).block();

        mealDto1 = mealDtoConverter.toDto(meal1);
        mealDto2 = mealDtoConverter.toDto(meal2);
    }

}