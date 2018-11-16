package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.BaseEntity;
import com.piotrek.diet.product.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
public class Meal extends BaseEntity {

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

    public Meal(String id) {
        super(id);
    }
}
