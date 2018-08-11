package com.piotrek.diet.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document
public class Product {

    @Id
    @NotNull
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
    private Double fibre = 0.0;

    @NotNull
    private Double kcal;

    private Double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate
    private Double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein
}