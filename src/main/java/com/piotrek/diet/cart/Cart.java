package com.piotrek.diet.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.product.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Document
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Cart {

    @Id
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private int targetUserCalories;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private ArrayList<Meal> meals = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();

    public Cart(String userId, LocalDate date, int targetUserCalories) {
        this.userId = userId;
        this.date = date;
        this.targetUserCalories = targetUserCalories;
    }

    public Cart(String id, String userId, int targetUserCalories, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.targetUserCalories = targetUserCalories;
        this.date = date;
    }

}
