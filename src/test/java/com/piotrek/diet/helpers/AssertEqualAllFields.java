package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.meal.MealDto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AssertEqualAllFields {

    public static void assertCartFields(CartDto expected, CartDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size(), "Cart meal list size"),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Cart products list size"),
                () -> assertEquals(expected.getAllProducts(), actual.getAllProducts(), "Cart all products list"),
                () -> assertEquals(expected.getUserId(), actual.getUserId()),
                () -> assertEquals(expected.getDate(), actual.getDate())
        );
    }

    public static void assertMealFields(MealDto expected, MealDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getRecipe(), actual.getRecipe()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }
}
