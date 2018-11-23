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
                () -> assertEquals(expected.getId(), actual.getId(), "CART: id is not equal"),
                () -> assertEquals(expected.getUserId(), actual.getUserId(), "CART: userId is not equal"),
                () -> assertEquals(expected.getTargetUserCalories(), actual.getTargetUserCalories(),
                        "CART: targetUserCalories are not equal"),
                () -> assertEquals(expected.getTargetUserProtein(), actual.getTargetUserProtein(),
                        "CART: targetUserProtein is not equal"),
                () -> assertEquals(expected.getTargetUserCarbohydrate(), actual.getTargetUserCarbohydrate(),
                        "CART: targetUserCarbohydrate is not equal"),
                () -> assertEquals(expected.getTargetUserFat(), actual.getTargetUserFat(),
                        "CART: targetUserFat is not equal"),
                () -> assertEquals(expected.getDate(), actual.getDate(), "CART: date is not equal"),
                () -> assertEquals(expected.getMeals(), actual.getMeals(), "CART: mealList is not equal"),
                () -> assertEquals(expected.getProducts(), actual.getProducts(), "CART: productList is not equal")
        );
    }


    public static void assertCartFields(CartDto expected, CartDto actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId(), "CART: id is not equal"),
                () -> assertEquals(expected.getUserId(), actual.getUserId(), "CART: userId is not equal"),
                () -> assertEquals(expected.getTargetUserCalories(), actual.getTargetUserCalories(),
                        "CART: targetUserCalories are not equal"),
                () -> assertEquals(expected.getTargetUserProtein(), actual.getTargetUserProtein(),
                        "CART: targetUserProtein is not equal"),
                () -> assertEquals(expected.getTargetUserCarbohydrate(), actual.getTargetUserCarbohydrate(),
                        "CART: targetUserCarbohydrate is not equal"),
                () -> assertEquals(expected.getTargetUserFat(), actual.getTargetUserFat(),
                        "CART: targetUserFat is not equal"),
                () -> assertEquals(expected.getDate(), actual.getDate(), "CART: date is not equal"),
                () -> assertEquals(expected.getProtein(), actual.getProtein(), "CART: protein is not eaual"),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate(), "CART: carbohydate is not equal"),
                () -> assertEquals(expected.getFat(), actual.getFat(), "CART: fat is not equal"),
                () -> assertEquals(expected.getKcal(), actual.getKcal(), "CART: kcal is not equal"),
                () -> assertEquals(expected.getItemCounter(), actual.getItemCounter(), "CART: itemCounter is not equal"),
                () -> assertEquals(expected.getMeals().size(), actual.getMeals().size(), "CART: meal list size"),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size(), "CART: products list size"),
                () -> assertEquals(expected.getAllProducts(), actual.getAllProducts(), "CART: all products list")
        );
    }

    public static void assertMealFields(MealDto expected, MealDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId(), "Meal id is not equal"),
                () -> assertEquals(expected.getName(), actual.getName(), "Meal name is not equal"),
                () -> assertEquals(expected.getDescription(), actual.getDescription(), "Meal description is not equal"),
                () -> assertEquals(expected.getRecipe(), actual.getRecipe(), "Meal recipe is not equal"),
                () -> assertEquals(expected.getProtein(), actual.getProtein(), "Meal protein value is not equal"),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate(), "Meal carbohydrate value is not equal"),
                () -> assertEquals(expected.getFat(), actual.getFat(), "Meal fat value is not equal"),
                () -> assertEquals(expected.getFibre(), actual.getFibre(), "Meal fibre value is not equal"),
                () -> assertEquals(expected.getKcal(), actual.getKcal(), "Meal kcal value is not equal"),
                () -> assertEquals(expected.getAmount(), actual.getAmount(), "Meal amount is not equal"),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl(),  "Meal image url is not equal"),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange(), "Meal carboExchange is not equal"),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent(), "Meal proteinAndFatEq is not equal"),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Meal produstsList is not equal"),
                () -> assertEquals(expected.getUserId(), actual.getUserId(), "Meal userId is not equal")
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
                () -> assertEquals(expected.getId(), actual.getId(), "id is not equal"),
                () -> assertEquals(expected.getFacebookId(), actual.getFacebookId(), "facebook id is not equal"),
                () -> assertEquals(expected.getUsername(), actual.getUsername(), "username is not equal"),
                () -> assertEquals(expected.getEmail(), actual.getEmail(), "email is not equal"),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName(), "first name is not equal"),
                () -> assertEquals(expected.getLastName(), actual.getLastName(), "last name is not equal"),
                () -> assertEquals(expected.getPictureUrl(), actual.getPictureUrl(), "picture url is not equal"),
                () -> assertEquals(expected.getSex(), actual.getSex(), "sex is not equal"),
                () -> assertEquals(expected.getActivity(), actual.getActivity(), "activity is not equal"),
                () -> assertEquals(expected.getAge(), actual.getAge(), "age is not equal"),
                () -> assertEquals(expected.getHeight(), actual.getHeight(), "height is not equal"),
                () -> assertEquals(expected.getWeight(), actual.getWeight(), "weight is not equal"),
                () -> assertEquals(expected.getCaloriesPerDay(), actual.getCaloriesPerDay(), "calories per day is not equal"),
                () -> assertEquals(expected.getProteinPerDay(), actual.getProteinPerDay(), "protein per day is not equal"),
                () -> assertEquals(expected.getCarbohydratePerDay(), actual.getCarbohydratePerDay(), "carbohydrate per day is not equal"),
                () -> assertEquals(expected.getFatPerDay(), actual.getFatPerDay(), "fat per day is not equal"),
                () -> assertEquals(expected.getLastVisit(), actual.getLastVisit(), "last visit is not equal"),
                () -> assertEquals(expected.getRole(), actual.getRole(), "role is not equal"),
                () -> assertEquals(expected.getFavouriteMeals().size(), actual.getFavouriteMeals().size(), "favourites list is not equal")
        );
    }

    public static void assertUserFields(UserDto expected, UserDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId(), "id is not equal"),
                () -> assertEquals(expected.getUsername(), actual.getUsername(), "username is not equal"),
                () -> assertEquals(expected.getEmail(), actual.getEmail(), "email is not equal"),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName(), "first name is not equal"),
                () -> assertEquals(expected.getLastName(), actual.getLastName(), "last name is not equal"),
                () -> assertEquals(expected.getPicture_url(), actual.getPicture_url(), "picture url is not equal"),
                () -> assertEquals(expected.getSex(), actual.getSex(), "sex is not equal"),
                () -> assertEquals(expected.getActivity(), actual.getActivity(), "activity is not equal"),
                () -> assertEquals(expected.getAge(), actual.getAge(), "age is not equal"),
                () -> assertEquals(expected.getHeight(), actual.getHeight(), "height is not equal"),
                () -> assertEquals(expected.getWeight(), actual.getWeight(), "weight is not equal"),
                () -> assertEquals(expected.getCaloriesPerDay(), actual.getCaloriesPerDay(), "calories per day is not equal"),
                () -> assertEquals(expected.getProteinPerDay(), actual.getProteinPerDay(), "protein per day is not equal"),
                () -> assertEquals(expected.getCarbohydratePerDay(), actual.getCarbohydratePerDay(), "carbohydrate per day is not equal"),
                () -> assertEquals(expected.getFatPerDay(), actual.getFatPerDay(), "fat per day is not equal")
        );
    }
}
