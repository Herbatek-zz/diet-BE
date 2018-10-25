package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.MealEquals;
import com.piotrek.diet.product.ProductDtoConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Arrays;

import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealDtoConverterTest {

    private MealDtoConverter mealDtoConverter;

    private Meal meal;
    private MealDto mealDto;

    @BeforeAll
    void setup() {
        mealDtoConverter = new MealDtoConverter(new ProductDtoConverter());

        meal = dumplingsWithId();
        meal.setProducts(new ArrayList<>(Arrays.asList(breadWithId(), bananaWithId())));

        mealDto = dumplingsWithIdDto();
        mealDto.setProducts(new ArrayList<>(Arrays.asList(breadWithIdDto(), bananaWithIdDto())));
    }

    @Test
    @DisplayName("Convert entity meal to dto")
    void toDto() {
        final var convertedMeal = mealDtoConverter.toDto(meal);

        assertNotNull(convertedMeal);
        assertTrue(MealEquals.mealDtoEquals(mealDto, convertedMeal));
    }

    @Test
    @DisplayName("Convert dto to meal entity")
    void fromDto() {
        final var convertedMeal = mealDtoConverter.fromDto(mealDto);

        assertNotNull(convertedMeal);
        assertTrue(MealEquals.mealEquals(meal, convertedMeal));
    }

    @Test
    @DisplayName("Convert meal list to dto list")
    void listToDto() {
        final var beforeConvert = new ArrayList<Meal>(Arrays.asList(dumplingsWithId(), coffeeWithId()));
        final var afterConvert = mealDtoConverter.listToDto(beforeConvert);
        final var expected = new ArrayList<MealDto>(Arrays.asList(dumplingsWithIdDto(), coffeeWithIdDto()));

        assertAll(
                () -> assertTrue(MealEquals.mealDtoEquals(expected.get(0), afterConvert.get(0))),
                () -> assertTrue(MealEquals.mealDtoEquals(expected.get(1), afterConvert.get(1)))
        );
    }

    @Test
    @DisplayName("Convert entity meal list from dto list")
    void listFromDto() {
        final var beforeConvert = new ArrayList<MealDto>(Arrays.asList(dumplingsWithIdDto(), coffeeWithIdDto()));
        final var afterConvert = mealDtoConverter.listFromDto(beforeConvert);
        final var expected = new ArrayList<Meal>(Arrays.asList(dumplingsWithId(), coffeeWithId()));

        assertAll(
                () -> assertTrue(MealEquals.mealEquals(expected.get(0), afterConvert.get(0))),
                () -> assertTrue(MealEquals.mealEquals(expected.get(1), afterConvert.get(1)))
        );
    }
}