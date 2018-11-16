package com.piotrek.diet.user;

import org.springframework.stereotype.Component;

import static com.piotrek.diet.product.enums.Macronutrient.*;

@Component
public class MacronutrientCalculator {

    int calculateDailyProtein(int caloriesPerDay) {
        return (int) (0.25 * caloriesPerDay) / Protein.getKcal();
    }

    int calculateDailyCarbohydrate(int caloriesPerDay) {
        return (int) (0.5 * caloriesPerDay) / Carbohydrate.getKcal();
    }

    int calculateDailyFat(int caloriesPerDay) {
        return (int) (0.25 * caloriesPerDay) / Fat.getKcal();
    }
}
