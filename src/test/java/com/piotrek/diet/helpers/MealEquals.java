package com.piotrek.diet.helpers;

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
        if (firstMeal.getProducts().size() != secondMeal.getProducts().size())
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
        if (!firstMealDto.getId().equals(secondMealDto.getId())) {
            System.err.println("Id is not the same");
            System.out.println(firstMealDto.getId());
            System.out.println(secondMealDto.getId());
            return false;
        }
        if (!firstMealDto.getName().equals(secondMealDto.getName())) {
            System.err.println("Name is not the same");
            System.out.println(firstMealDto.getName());
            System.out.println(secondMealDto.getName());
            return false;
        }
        if (!firstMealDto.getDescription().equals(secondMealDto.getDescription())) {
            System.err.println("Description is not the same");
            System.out.println(firstMealDto.getDescription());
            System.out.println(secondMealDto.getDescription());
            return false;
        }
        if (firstMealDto.getAmount() != secondMealDto.getAmount()) {
            System.err.println("Amount is not the same");
            System.out.println(firstMealDto.getAmount());
            System.out.println(secondMealDto.getAmount());
            return false;
        }
        if (!firstMealDto.getRecipe().equals(secondMealDto.getRecipe())) {
            System.err.println("Recipe is not the same");
            System.out.println(firstMealDto.getRecipe());
            System.out.println(secondMealDto.getRecipe());
            return false;
        }
        if (!firstMealDto.getImageUrl().equals(secondMealDto.getImageUrl())) {
            System.err.println("ImageUrl is not the same");
            System.out.println(firstMealDto.getImageUrl());
            System.out.println(secondMealDto.getImageUrl());
            return false;
        }
        if (firstMealDto.getProtein() != secondMealDto.getProtein()) {
            System.err.println("Protein are not the same");
            System.out.println(firstMealDto.getProtein());
            System.out.println(secondMealDto.getProtein());
            return false;
        }
        if (firstMealDto.getProteinAndFatEquivalent() != secondMealDto.getProteinAndFatEquivalent()) {
            System.err.println("ProteinAndFatEquivalent is not the same");
            System.out.println(firstMealDto.getProteinAndFatEquivalent());
            System.out.println(secondMealDto.getProteinAndFatEquivalent());
            return false;
        }
        if (firstMealDto.getCarbohydrate() != secondMealDto.getCarbohydrate()) {
            System.err.println("Carbohydrates are not the same");
            System.out.println(firstMealDto.getCarbohydrate());
            System.out.println(secondMealDto.getCarbohydrate());
            return false;
        }
        if (firstMealDto.getCarbohydrateExchange() != secondMealDto.getCarbohydrateExchange()) {
            System.err.println("CarbohydrateExchange is not the same");
            System.out.println(firstMealDto.getCarbohydrateExchange());
            System.out.println(secondMealDto.getCarbohydrateExchange());
            return false;
        }
        if (firstMealDto.getFat() != secondMealDto.getFat()) {
            System.err.println("Fat is not the same");
            System.out.println(firstMealDto.getFat());
            System.out.println(secondMealDto.getFat());
            return false;
        }
        if (firstMealDto.getFibre() != secondMealDto.getFibre()) {
            System.err.println("Fibre is not the same");
            System.out.println(firstMealDto.getFibre());
            System.out.println(secondMealDto.getFibre());
            return false;
        }
        if (firstMealDto.getKcal() != secondMealDto.getKcal()) {
            System.err.println("Kcal are not the same");
            System.out.println(firstMealDto.getKcal());
            System.out.println(secondMealDto.getKcal());
            return false;
        }
        if (!firstMealDto.getUserId().equals(secondMealDto.getUserId())) {
            System.err.println("UserId is not the same");
            System.out.println(firstMealDto.getUserId());
            System.out.println(secondMealDto.getUserId());
            return false;
        }
        if (firstMealDto.getProducts().size() != secondMealDto.getProducts().size()) {
            System.err.println("Products list size is not the same");
            System.out.println(firstMealDto.getProducts().size());
            System.out.println(secondMealDto.getProducts().size());
        }
        return true;
    }
}
