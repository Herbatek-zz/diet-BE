package com.piotrek.diet.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.ProductDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class CartDto {

    private String id;

    @NotNull
    private String userId;

    @NotNull
    private int targetUserCalories;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private int itemCounter;

    private ArrayList<MealDto> meals = new ArrayList<>();
    private ArrayList<ProductDto> products = new ArrayList<>();
    private ArrayList<ProductDto> allProducts = new ArrayList<>();

    public CartDto(String userId, LocalDate date, int targetUserCalories) {
        this.userId = userId;
        this.date = date;
        this.targetUserCalories = targetUserCalories;
    }
}
