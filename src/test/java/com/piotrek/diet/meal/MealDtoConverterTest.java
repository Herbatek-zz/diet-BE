package com.piotrek.diet.meal;

import com.piotrek.diet.product.ProductDtoConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertMealFields;
import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealDtoConverterTest {

    private MealDtoConverter mealDtoConverter;

    private Meal meal;
    private MealDto mealDto;

    @BeforeAll
    void setup() {
        mealDtoConverter = new MealDtoConverter(new ProductDtoConverter());

        meal = dumplingsWithId();
        meal.setProducts(new ArrayList<>(List.of(breadWithId(), bananaWithId())));

        mealDto = dumplingsWithIdDto();
        mealDto.setProducts(new ArrayList<>(List.of(breadWithIdDto(), bananaWithIdDto())));
    }

    @Test
    @DisplayName("Convert entity meal to dto")
    void toDto() {
        final var convertedMeal = mealDtoConverter.toDto(meal);
        assertMealFields(mealDto, convertedMeal);
    }

    @Test
    @DisplayName("Convert dto to meal entity")
    void fromDto() {
        final var convertedMeal = mealDtoConverter.fromDto(mealDto);
        assertMealFields(meal, convertedMeal);
    }

    @Test
    @DisplayName("Convert meal list to dto list")
    void listToDto() {
        final var expected = List.of(dumplingsWithIdDto(), coffeeWithIdDto());
        final var convertedList = mealDtoConverter.listToDto(List.of(dumplingsWithId(), coffeeWithId()));

        assertAll(
                () -> assertMealFields(expected.get(0), convertedList.get(0)),
                () -> assertMealFields(expected.get(1), convertedList.get(1))
        );
    }

    @Test
    @DisplayName("Convert entity meal list from dto list")
    void listFromDto() {
        final var expected = new ArrayList<>(Arrays.asList(dumplingsWithId(), coffeeWithId()));
        final var afterConvert = mealDtoConverter.listFromDto(List.of(dumplingsWithIdDto(), coffeeWithIdDto()));

        assertAll(
                () -> assertMealFields(expected.get(0), afterConvert.get(0)),
                () -> assertMealFields(expected.get(1), afterConvert.get(1))
        );
    }
}