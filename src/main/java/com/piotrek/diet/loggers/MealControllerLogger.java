package com.piotrek.diet.loggers;

import com.piotrek.diet.meal.MealDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.piotrek.diet.loggers.Constants.LOGGER_PREFIX;

@Slf4j
@Aspect
@Component
public class MealControllerLogger {

    @Before(value = "execution(* com.piotrek.diet.meal.MealController.findAll(..)) && args(page, size)", argNames = "page,size")
    public void logBeforeFindAllMeals(int page, int size) {
        log.info(LOGGER_PREFIX + "Attempt find all meals [page = " + page + ", size = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.meal.MealController.findById(..)) && args(id)")
    public void logBeforeFindByIdMeal(String id) {
        log.info(LOGGER_PREFIX + "Attempt to find a meal [id = " + id + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.meal.MealController.searchByName(..)) && args(page, size, query)", argNames = "page,size,query")
    public void logBeforeSearchByNameMeal(int page, int size, String query) {
        log.info(LOGGER_PREFIX + "Attempt to search meals by name [name = " + query + ", page = " + page
                + ", size = " + size + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.meal.MealController.editMeal(..)) && args(id, mealDto)", argNames = "id,mealDto")
    public void logBeforeEditMeal(String id, MealDto mealDto) {
        log.info(LOGGER_PREFIX + "Attempt to edit a meal [id = " + id + ", name = " + mealDto.getName() + "]");
    }

    @Before(value = "execution(* com.piotrek.diet.meal.MealController.deleteById(..)) && args(id)")
    public void logBeforeDeleteByIdMeal(String id) {
        log.info(LOGGER_PREFIX + "Attempt to delete a meal [id = " + id + "]");
    }
}
