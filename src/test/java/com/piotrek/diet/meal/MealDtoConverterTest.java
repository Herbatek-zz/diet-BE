package com.piotrek.diet.meal;

import com.piotrek.diet.product.ProductDtoConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Arrays;

import static com.piotrek.diet.sample.MealSample.*;
import static com.piotrek.diet.sample.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealDtoConverterTest {

    private MealDtoConverter mealDtoConverter;

    private Meal meal;
    private MealDto meaLDto;

    @BeforeAll
    void setup() {
        mealDtoConverter = new MealDtoConverter(new ProductDtoConverter());

        meal = dumplingsWithId();
        meal.setProducts(new ArrayList<>(Arrays.asList(breadWithId(), bananaWithId())));

        meaLDto = dumplingsWithIdDto();
        meaLDto.setProducts(new ArrayList<>(Arrays.asList(breadWithIdDto(), bananaWithIdDto())));
    }

    @Test
    @DisplayName("Convert entity meal to dto")
    void toDto() {
        final var convertedMeal = mealDtoConverter.toDto(meal);

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
                () -> assertEquals(meaLDto.getAmount(), convertedMeal.getAmount()),
                () -> assertEquals(meaLDto.getCarbohydrateExchange(), convertedMeal.getCarbohydrateExchange()),
                () -> assertEquals(meaLDto.getProteinAndFatEquivalent(), convertedMeal.getProteinAndFatEquivalent()),
                () -> assertEquals(meaLDto.getImageUrl(), convertedMeal.getImageUrl()),
                () -> assertEquals(meaLDto.getProducts(), convertedMeal.getProducts()),
                () -> assertEquals(meaLDto.getUserId(), convertedMeal.getUserId())
        );
    }

    @Test
    @DisplayName("Convert dto to meal entity")
    void fromDto() {
        final var convertedMeal = mealDtoConverter.fromDto(meaLDto);

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
                () -> assertEquals(meal.getAmount(), convertedMeal.getAmount()),
                () -> assertEquals(meal.getCarbohydrateExchange(), convertedMeal.getCarbohydrateExchange()),
                () -> assertEquals(meal.getProteinAndFatEquivalent(), convertedMeal.getProteinAndFatEquivalent()),
                () -> assertEquals(meal.getImageUrl(), convertedMeal.getImageUrl()),
                () -> assertEquals(meal.getProducts(), convertedMeal.getProducts()),
                () -> assertEquals(meal.getUserId(), convertedMeal.getUserId())
        );
    }

    @Test
    @DisplayName("Convert meal list to dto list")
    void listToDto() {
        final var beforeConvert = new ArrayList<Meal>(Arrays.asList(dumplingsWithId(), coffeeWithId()));
        final var afterConvert = mealDtoConverter.listToDto(beforeConvert);

        assertAll(
                () -> assertNotNull(afterConvert),
                () -> assertNotNull(afterConvert.get(0)),
                () -> assertNotNull(afterConvert.get(1)),
                () -> assertEquals(beforeConvert.size(), afterConvert.size()),
                () -> assertEquals(beforeConvert.get(0).getId(), afterConvert.get(0).getId()),
                () -> assertEquals(beforeConvert.get(1).getId(), afterConvert.get(1).getId()),
                () -> assertEquals(beforeConvert.get(0).getClass(), Meal.class),
                () -> assertEquals(afterConvert.get(0).getClass(), MealDto.class)
        );
    }

    @Test
    @DisplayName("Convert entity meal list from dto list")
    void listFromDto() {
        final var beforeConvert = new ArrayList<MealDto>(Arrays.asList(dumplingsWithIdDto(), coffeeWithIdDto()));
        final var afterConvert = mealDtoConverter.listFromDto(beforeConvert);

        assertAll(
                () -> assertNotNull(afterConvert),
                () -> assertNotNull(afterConvert.get(0)),
                () -> assertNotNull(afterConvert.get(1)),
                () -> assertEquals(beforeConvert.size(), afterConvert.size()),
                () -> assertEquals(beforeConvert.get(0).getId(), afterConvert.get(0).getId()),
                () -> assertEquals(beforeConvert.get(1).getId(), afterConvert.get(1).getId()),
                () -> assertEquals(beforeConvert.get(0).getClass(), MealDto.class),
                () -> assertEquals(afterConvert.get(0).getClass(), Meal.class)
        );
    }
}