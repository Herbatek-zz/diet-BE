package com.piotrek.diet.product;

import com.piotrek.diet.helpers.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
public class Product extends BaseEntity {

    @NotNull
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

    @NotNull
    private String userId;
}
