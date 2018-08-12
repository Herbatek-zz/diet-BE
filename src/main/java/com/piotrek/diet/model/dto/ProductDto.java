package com.piotrek.diet.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductDto {

    private String id;

    @NotNull
    private String name;
    private String description;
    private String imageUrl;

    @NotNull
    private Double protein;

    @NotNull
    private Double carbohydrate;

    @NotNull
    private Double fat;

    private Double fibre;

    @NotNull
    private Double kcal;

    private Double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate
    private Double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein
}
