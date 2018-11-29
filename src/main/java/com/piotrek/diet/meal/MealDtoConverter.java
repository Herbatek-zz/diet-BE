package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.DtoConverter;
import com.piotrek.diet.product.ProductDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealDtoConverter implements DtoConverter<Meal, MealDto> {

    private final ProductDtoConverter productDtoConverter;

    @Override
    public MealDto toDto(Meal meal) {
        var mealDto = new MealDto();
        mealDto.setId(meal.getId());
        mealDto.setName(meal.getName());
        mealDto.setDescription(meal.getDescription());
        mealDto.setRecipe(meal.getRecipe());
        mealDto.setProtein(meal.getProtein());
        mealDto.setCarbohydrate(meal.getCarbohydrate());
        mealDto.setFat(meal.getFat());
        mealDto.setFibre(meal.getFibre());
        mealDto.setKcal(meal.getKcal());
        mealDto.setAmount(meal.getAmount());
        mealDto.setImageUrl(meal.getImageUrl());
        mealDto.setCarbohydrateExchange(meal.getCarbohydrateExchange());
        mealDto.setProteinAndFatEquivalent(meal.getProteinAndFatEquivalent());
        mealDto.setProducts(productDtoConverter.listToDto(meal.getProducts()));
        mealDto.setUserId(meal.getUserId());
        return mealDto;
    }

    @Override
    public Meal fromDto(MealDto mealDto) {
        var meal = new Meal();
        meal.setId(mealDto.getId());
        meal.setName(mealDto.getName());
        meal.setDescription(mealDto.getDescription());
        meal.setRecipe(mealDto.getRecipe());
        meal.setProtein(mealDto.getProtein());
        meal.setCarbohydrate(mealDto.getCarbohydrate());
        meal.setFat(mealDto.getFat());
        meal.setFibre(mealDto.getFibre());
        meal.setKcal(mealDto.getKcal());
        meal.setAmount(mealDto.getAmount());
//        meal.setImageUrl(mealDto.getImageUrl());
        meal.setCarbohydrateExchange(mealDto.getCarbohydrateExchange());
        meal.setProteinAndFatEquivalent(mealDto.getProteinAndFatEquivalent());
        meal.setProducts(productDtoConverter.listFromDto(mealDto.getProducts()));
        meal.setUserId(mealDto.getUserId());
        return meal;
    }

    public ArrayList<MealDto> listToDto(List<Meal> meals) {
        return meals
                .stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Meal> listFromDto(List<MealDto> mealDtos) {
        return mealDtos
                .stream()
                .map(this::fromDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
