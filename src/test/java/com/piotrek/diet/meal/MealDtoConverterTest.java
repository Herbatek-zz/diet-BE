package com.piotrek.diet.meal;

import com.piotrek.diet.sample.MealSample;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MealDtoConverterTest {

    private MealDtoConverter mealDtoConverter = new MealDtoConverter();

    private Meal meal = MealSample.dumplingsWithId();
    private MealDto meaLDto = MealSample.dumplingsWithIdDto();

    @Test
    void toDto() {
        var convertedMeal = mealDtoConverter.toDto(meal);

        assertNotNull(convertedMeal);
        assertAll(
                () -> assertEquals(meaLDto.getId(), convertedMeal.getId()),
                () -> assertEquals(meaLDto.getName(), convertedMeal.getName()),
                () -> assertEquals(meaLDto.getDescription(), convertedMeal.getDescription()),
                () -> assertEquals(meaLDto.getRecipe(), convertedMeal.getRecipe()),
                () -> assertEquals(meaLDto.getProtein(), convertedMeal.getProtein()),
                () -> assertEquals(meaLDto.getCarbohydrate(), convertedMeal.getCarbohydrate()),
                () -> assertEquals(meaLDto.getFat(), convertedMeal.getFat()),
                () -> assertEquals(meaLDto.getFibre(), convertedMeal.getFibre()),
                () -> assertEquals(meaLDto.getKcal(), convertedMeal.getKcal()),
                () -> assertEquals(meaLDto.getCarbohydrateExchange(), convertedMeal.getCarbohydrateExchange()),
                () -> assertEquals(meaLDto.getProteinAndFatEquivalent(), convertedMeal.getProteinAndFatEquivalent()),
                () -> assertEquals(meaLDto.getImageUrl(), convertedMeal.getImageUrl())
        );
    }

    @Test
    void fromDto() {
        var convertedMeal = mealDtoConverter.fromDto(meaLDto);

        assertNotNull(convertedMeal);
        assertAll(
                () -> assertEquals(meal.getId(), convertedMeal.getId()),
                () -> assertEquals(meal.getName(), convertedMeal.getName()),
                () -> assertEquals(meal.getDescription(), convertedMeal.getDescription()),
                () -> assertEquals(meal.getRecipe(), convertedMeal.getRecipe()),
                () -> assertEquals(meal.getProtein(), convertedMeal.getProtein()),
                () -> assertEquals(meal.getCarbohydrate(), convertedMeal.getCarbohydrate()),
                () -> assertEquals(meal.getFat(), convertedMeal.getFat()),
                () -> assertEquals(meal.getFibre(), convertedMeal.getFibre()),
                () -> assertEquals(meal.getKcal(), convertedMeal.getKcal()),
                () -> assertEquals(meal.getCarbohydrateExchange(), convertedMeal.getCarbohydrateExchange()),
                () -> assertEquals(meal.getProteinAndFatEquivalent(), convertedMeal.getProteinAndFatEquivalent()),
                () -> assertEquals(meal.getImageUrl(), convertedMeal.getImageUrl())
        );
    }
}