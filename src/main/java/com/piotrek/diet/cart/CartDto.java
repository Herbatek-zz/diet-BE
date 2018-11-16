package com.piotrek.diet.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.piotrek.diet.helpers.BaseDto;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.ProductDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.piotrek.diet.helpers.Constants.DATE_FORMAT;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CartDto extends BaseDto {

    @NotNull
    private String userId;

    @NotNull
    private int targetUserCalories;

    @NotNull
    private int targetUserProtein;

    @NotNull
    private int targetUserCarbohydrate;

    @NotNull
    private int targetUserFat;

    @NotNull
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDate date;

    private int itemCounter;

    private double protein;
    private double carbohydrate;
    private double fat;
    private double fibre;
    private double carbohydrateExchange;
    private double proteinAndFatEquivalent;
    private double kcal;

    private ArrayList<MealDto> meals = new ArrayList<>();
    private ArrayList<ProductDto> products = new ArrayList<>();
    private ArrayList<ProductDto> allProducts = new ArrayList<>();

    public CartDto(String userId, LocalDate date, int targetUserCalories, int targetUserCarbohydrate, int targetUserProtein,
                   int targetUserFat) {
        this.userId = userId;
        this.date = date;
        this.targetUserCalories = targetUserCalories;
        this.targetUserCarbohydrate = targetUserCarbohydrate;
        this.targetUserProtein = targetUserProtein;
        this.targetUserFat = targetUserFat;
    }

    public CartDto(String id, String userId, int targetUserCalories, LocalDate date) {
        super(id);
        this.userId = userId;
        this.targetUserCalories = targetUserCalories;
        this.date = date;
    }
}
