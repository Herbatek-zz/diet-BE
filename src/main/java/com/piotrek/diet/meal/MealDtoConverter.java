package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.DtoConverter;
import org.springframework.stereotype.Component;

@Component
public class MealDtoConverter implements DtoConverter<Meal, MealDto> {

    @Override
    public MealDto toDto(Meal entity) {
        return null;
    }

    @Override
    public Meal fromDto(MealDto dto) {
        return null;
    }
}
