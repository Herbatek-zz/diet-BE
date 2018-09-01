package com.piotrek.diet.meal;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MealDto {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String recipe;

    private double protein;
    private double carbohydrate;
    private double fat;
    private double fibre;
    private double kcal;

    private String imageUrl;
}
