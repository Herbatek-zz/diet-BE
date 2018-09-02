package com.piotrek.diet.meal;

import com.piotrek.diet.product.Product;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;

@Data
@Document
public class Meal {

    @Id
    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String recipe;

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

    private String imageUrl;

    @NotNull
    private double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate

    @NotNull
    private double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein

    private HashMap<String, String> products;

    @NotNull
    private String userId;
}
