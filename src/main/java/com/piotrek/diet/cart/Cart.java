package com.piotrek.diet.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.piotrek.diet.helpers.BaseEntity;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class Cart extends BaseEntity {

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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private ArrayList<Meal> meals = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();

    public Cart(String userId, LocalDate date, int targetUserCalories, int targetUserCarbohydrate, int targetUserProtein,
                int targetUserFat) {
        this.userId = userId;
        this.date = date;
        this.targetUserCalories = targetUserCalories;
        this.targetUserCarbohydrate = targetUserCarbohydrate;
        this.targetUserProtein = targetUserProtein;
        this.targetUserFat = targetUserFat;
    }

    public Cart(String id, String userId, int targetUserCalories, LocalDate date) {
        super(id);
        this.userId = userId;
        this.targetUserCalories = targetUserCalories;
        this.date = date;
    }

}
