package com.piotrek.diet.meal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

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
    private String userId;
}
