package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.BaseDto;
import com.piotrek.diet.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
public class MealDto extends BaseDto {

    @NotNull
    @Size(min = 2, max = 60)
    private String name;

    @NotNull
    @Size(min = 10, max = 3000)
    private String description;

    @NotNull
    @Size(min = 10, max = 3000)
    private String recipe;

    @Min(0)
    private double protein;

    @Min(0)
    private double carbohydrate;

    @Min(0)
    private double fat;

    @Min(0)
    private double fibre;

    @Min(0)
    private double kcal;

    @Min(0)
    private int amount;

    @Min(0)
    private double carbohydrateExchange;    // <-- 1.0 == 10g carbohydrate

    @Min(0)
    private double proteinAndFatEquivalent; // <-- 1.0 == 100kcal from fat and protein

    @NotNull
    private String imageUrl;

    private ArrayList<ProductDto> products = new ArrayList<>();

    private String userId;

    public MealDto(String id) {
        super(id);
    }

}
