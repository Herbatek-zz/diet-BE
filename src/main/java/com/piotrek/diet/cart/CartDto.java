package com.piotrek.diet.cart;

import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.ProductDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@EqualsAndHashCode(of = {"id"})
public class CartDto {

    private String id;

    @NotNull
    private LocalDate date;

    private ArrayList<MealDto> meals = new ArrayList<>();
    private ArrayList<ProductDto> products = new ArrayList<>();

    @NotNull
    private String userId;
}
