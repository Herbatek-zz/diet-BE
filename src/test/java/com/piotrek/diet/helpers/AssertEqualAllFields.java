package com.piotrek.diet.helpers;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserDto;

import static org.junit.jupiter.api.Assertions.*;

public class AssertEqualAllFields {

    public static void assertCartFields(Cart expected, Cart actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId(), "id is not equal"),
                () -> assertEquals(expected.getUserId(), actual.getUserId(), "userId is not equal"),
                () -> assertEquals(expected.getTargetUserCalories(), actual.getTargetUserCalories(),
                        "targetUserCalories are not equal"),
                () -> assertEquals(expected.getDate(), actual.getDate(), "date is not equal"),
                () -> assertEquals(expected.getMeals(), actual.getMeals(), "mealList is not equal"),
                () -> assertEquals(expected.getProducts(), actual.getProducts(), "productList is not equal")
        );
    }

    public static void assertCartFields(CartDto expected, CartDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId(), "id is not equal"),
                () -> assertEquals(expected.getUserId(), actual.getUserId(), "userId is not equal"),
                () -> assertEquals(expected.getTargetUserCalories(), actual.getTargetUserCalories(),
                        "targetUserCalories are not equal"),
                () -> assertEquals(expected.getDate(), actual.getDate()),
                () -> assertEquals(expected.getItemCounter(), actual.getItemCounter(), "itemCounter is not equal"),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size(), "Cart meal list size"),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Cart products list size"),
                () -> assertEquals(expected.getAllProducts(), actual.getAllProducts(), "Cart all products list")
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

    public static void assertMealFields(Meal expected, Meal actual) {
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

    public static void assertProductFields(Product expected, Product actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }

    public static void assertProductFields(ProductDto expected, ProductDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }

    public static void assertUserFields(User expected, User actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getFacebookId(), actual.getFacebookId()),
                () -> assertEquals(expected.getUsername(), actual.getUsername()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                () -> assertEquals(expected.getLastName(), actual.getLastName()),
                () -> assertEquals(expected.getPictureUrl(), actual.getPictureUrl()),
                () -> assertEquals(expected.getSex(), actual.getSex()),
                () -> assertEquals(expected.getActivity(), actual.getActivity()),
                () -> assertEquals(expected.getAge(), actual.getAge()),
                () -> assertEquals(expected.getHeight(), actual.getHeight()),
                () -> assertEquals(expected.getWeight(), actual.getWeight()),
                () -> assertEquals(expected.getCaloriesPerDay(), actual.getCaloriesPerDay()),
                () -> assertEquals(expected.getCreatedAt(), actual.getCreatedAt()),
                () -> assertEquals(expected.getLastVisit(), actual.getLastVisit()),
                () -> assertEquals(expected.getRole(), actual.getRole()),
                () -> assertEquals(expected.getFavouriteMeals().size(), actual.getFavouriteMeals().size())
        );
    }

    public static void assertUserFields(UserDto expected, UserDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getUsername(), actual.getUsername()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                () -> assertEquals(expected.getLastName(), actual.getLastName()),
                () -> assertEquals(expected.getPicture_url(), actual.getPicture_url()),
                () -> assertEquals(expected.getSex(), actual.getSex()),
                () -> assertEquals(expected.getActivity(), actual.getActivity()),
                () -> assertEquals(expected.getAge(), actual.getAge()),
                () -> assertEquals(expected.getHeight(), actual.getHeight()),
                () -> assertEquals(expected.getWeight(), actual.getWeight()),
                () -> assertEquals(expected.getCaloriesPerDay(), actual.getCaloriesPerDay())
        );
    }
}
