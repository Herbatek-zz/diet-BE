package com.piotrek.diet.sample;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;

public class MealEquals {

    public static boolean mealEquals(Meal firstMeal, Meal secondMeal) {
        if (!firstMeal.getId().equals(secondMeal.getId()))
            return false;
        if (!firstMeal.getName().equals(secondMeal.getName()))
            return false;
        if (!firstMeal.getDescription().equals(secondMeal.getDescription()))
            return false;
        if (firstMeal.getAmount() != secondMeal.getAmount())
            return false;
        if (!firstMeal.getRecipe().equals(secondMeal.getRecipe()))
            return false;
        if (!firstMeal.getImageUrl().equals(secondMeal.getImageUrl()))
            return false;
        if (!firstMeal.getProducts().equals(secondMeal.getProducts()))
            return false;
        if (firstMeal.getProtein() != secondMeal.getProtein())
            return false;
        if (firstMeal.getProteinAndFatEquivalent() != secondMeal.getProteinAndFatEquivalent())
            return false;
        if (firstMeal.getCarbohydrate() != secondMeal.getCarbohydrate())
            return false;
        if (firstMeal.getCarbohydrateExchange() != secondMeal.getCarbohydrateExchange())
            return false;
        if (firstMeal.getFat() != secondMeal.getFat())
            return false;
        if (firstMeal.getFibre() != secondMeal.getFibre())
            return false;
        if (firstMeal.getKcal() != secondMeal.getKcal())
            return false;
        if (!firstMeal.getUserId().equals(secondMeal.getUserId()))
            return false;
        return true;
    }

    public static boolean mealDtoEquals(MealDto firstMealDto, MealDto secondMealDto) {
        if (!firstMealDto.getId().equals(secondMealDto.getId()))
            return false;
        if (!firstMealDto.getName().equals(secondMealDto.getName()))
            return false;
        if (!firstMealDto.getDescription().equals(secondMealDto.getDescription()))
            return false;
        if (firstMealDto.getAmount() != secondMealDto.getAmount())
            return false;
        if (!firstMealDto.getRecipe().equals(secondMealDto.getRecipe()))
            return false;
        if (!firstMealDto.getImageUrl().equals(secondMealDto.getImageUrl()))
            return false;
        firstMealDto
                .getProducts()
                .forEach(productDto -> secondMealDto
                        .getProducts()
                        .forEach(productDto1 -> ProductEquals.productDtoEquals(productDto, productDto1)));
        if (firstMealDto.getProtein() != secondMealDto.getProtein())
            return false;
        if (firstMealDto.getProteinAndFatEquivalent() != secondMealDto.getProteinAndFatEquivalent())
            return false;
        if (firstMealDto.getCarbohydrate() != secondMealDto.getCarbohydrate())
            return false;
        if (firstMealDto.getCarbohydrateExchange() != secondMealDto.getCarbohydrateExchange())
            return false;
        if (firstMealDto.getFat() != secondMealDto.getFat())
            return false;
        if (firstMealDto.getFibre() != secondMealDto.getFibre())
            return false;
        if (firstMealDto.getKcal() != secondMealDto.getKcal())
            return false;
        if (!firstMealDto.getUserId().equals(secondMealDto.getUserId()))
            return false;
        return true;
    }
}
