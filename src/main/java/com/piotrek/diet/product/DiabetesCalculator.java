package com.piotrek.diet.product;

import org.springframework.stereotype.Component;

import static com.piotrek.diet.product.enums.Macronutrient.Fat;
import static com.piotrek.diet.product.enums.Macronutrient.Protein;

@Component
public class DiabetesCalculator {

    public double calculateCarbohydrateExchange(double carbohydrate, double fibre) {
        return (carbohydrate - fibre) / 10;
    }

    public double calculateProteinAndFatEquivalent(double protein, double fat) {
        return ((protein * Protein.getKcal()) + (fat * Fat.getKcal())) / 100;
    }
}
