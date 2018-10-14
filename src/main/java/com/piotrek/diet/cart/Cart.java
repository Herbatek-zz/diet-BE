package com.piotrek.diet.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.piotrek.diet.meal.Meal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private ArrayList<Meal> meals = new ArrayList<>();

    @NotNull
    private String userId;

    public Cart(String userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }


}
