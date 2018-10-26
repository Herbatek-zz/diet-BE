package com.piotrek.diet.meal;

import com.piotrek.diet.product.ProductDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Data
@EqualsAndHashCode(of = {"id"})
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
    private int amount;

    private double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate

    private double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein

    @NotNull
    private String imageUrl;

    private ArrayList<ProductDto> products = new ArrayList<>();

    private String userId;

    @Override
    public String toString() {
        return "{id=" + id + ", name=" + name + ", description=" + description + ", recipe=" + recipe + ", protein=" + protein + ", carbohydrate=" + carbohydrate + ", fat=" + fat + ", fibre=" + fibre + ", kcal=" + kcal + ", amount=" + amount + ", carbohydrateExchange=" + carbohydrateExchange + ", proteinAndFatEquivalent=" + proteinAndFatEquivalent + ", imageUrl=" + imageUrl + ", products=" + products + ", userId=" + userId + "}";
    }
}
