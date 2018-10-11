package com.piotrek.diet.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"id"})
public class ProductDto {

    private String id;

    @NotNull
    @Size(min = 2, max = 80)
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String imageUrl;

    @NotNull
    private double protein;

    @NotNull
    private double carbohydrate;

    @NotNull
    private double fat;

    @NotNull
    private double fibre;

    @NotNull
    private double kcal;

    private int amount;

    @NotNull
    private double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate

    @NotNull
    private double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein

    private String userId;
}
