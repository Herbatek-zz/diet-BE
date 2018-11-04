package com.piotrek.diet.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"id"})
public class ProductDto {

    private String id;

    @NotNull
    @Size(min = 2, max = 60)
    private String name;

    @NotNull
    @Size(min = 10, max = 3000)
    private String description;

    @NotNull
    private String imageUrl;

    @NotNull
    @Min(0)
    @Max(100)
    private double protein;

    @NotNull
    @Min(0)
    @Max(100)
    private double carbohydrate;

    @NotNull
    @Min(0)
    @Max(100)
    private double fat;

    @NotNull
    @Min(0)
    @Max(100)
    private double fibre;

    @NotNull
    @Min(0)
    @Max(1000)
    private double kcal;

    @Min(0)
    private int amount;

    @NotNull
    @Min(0)
    private double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate

    @NotNull
    @Min(0)
    private double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein

    private String userId;
}
