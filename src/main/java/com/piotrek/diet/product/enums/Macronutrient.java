package com.piotrek.diet.product.enums;

@SuppressWarnings("SpellCheckingInspection")
public enum Macronutrient {

    Protein(4),
    Carbohydrate(4),
    Fat(9);

    private int kcal;

    Macronutrient(int kcal) {
        this.kcal = kcal;
    }

    public int getKcal() {
        return kcal;
    }
}
