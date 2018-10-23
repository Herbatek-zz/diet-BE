package com.piotrek.diet.meal;

import com.piotrek.diet.product.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Data
@Document
@EqualsAndHashCode(of = {"id"})
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

    private double protein;

    private double carbohydrate;

    private double fat;

    private double fibre;

    private double kcal;

    private int amount;

    @NotNull
    private String imageUrl;

    private double carbohydrateExchange;

    private double proteinAndFatEquivalent;

    private ArrayList<Product> products = new ArrayList<>();

    @NotNull
    private String userId;
}
