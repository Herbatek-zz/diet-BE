package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.sample.MealSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    private MealDtoConverter mealDtoConverter = new MealDtoConverter();

    private MealService mealService;

    private Meal meal;

    @BeforeEach
    void beforeEach() {
        createMeal();
        MockitoAnnotations.initMocks(this);
        mealService = new MealService(mealRepository, mealDtoConverter);
    }

    @Test
    void findById_whenSuccess_thenReturnMeal() {
        Mockito.when(mealRepository.findById(meal.getId())).thenReturn(Mono.just(meal));

        Meal byId = mealService.findById(meal.getId()).block();

        assertNotNull(byId);
        assertAll(
                () -> assertEquals(meal.getId(), byId.getId()),
                () -> assertEquals(meal.getName(), byId.getName()),
                () -> assertEquals(meal.getDescription(), byId.getDescription()),
                () -> assertEquals(meal.getRecipe(), byId.getRecipe()),
                () -> assertEquals(meal.getImageUrl(), byId.getImageUrl()),
                () -> assertEquals(meal.getCarbohydrate(), byId.getCarbohydrate()),
                () -> assertEquals(meal.getFibre(), byId.getFibre()),
                () -> assertEquals(meal.getFat(), byId.getFat()),
                () -> assertEquals(meal.getProtein(), byId.getProtein()),
                () -> assertEquals(meal.getProteinAndFatEquivalent(), byId.getProteinAndFatEquivalent()),
                () -> assertEquals(meal.getCarbohydrateExchange(), byId.getCarbohydrateExchange()),
                () -> assertEquals(meal.getProducts(), byId.getProducts()),
                () -> assertEquals(meal.getUserId(), byId.getUserId()),
                () -> assertEquals(meal.getKcal(), byId.getKcal())
        );

        verify(mealRepository, times(1)).findById(meal.getId());
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void findById_whenNotFoundMeal_thenThrowNotFoundException() {
        var id = "@#@#@ID";
        Mockito.when(mealRepository.findById(id)).thenReturn(Mono.empty());

        assertThrows(NotFoundException.class, () -> mealService.findById(id).block());

        verify(mealRepository, times(1)).findById(id);
        verifyNoMoreInteractions(mealRepository);
    }

    @Test
    void findAllByUserId() {
    }

    @Test
    void findAllPageable() {
    }

    private void createMeal() {
        meal = MealSample.dumplingsWithId();
    }
}