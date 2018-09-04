package com.piotrek.diet.sample;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;

import java.util.UUID;

public class MealSample {

    public static final String DUMPLINGS_ID = UUID.randomUUID().toString();

    public static Meal dumplingsWithId() {
        var meal = dumplingsWithoutId();
        meal.setId(DUMPLINGS_ID);
        return meal;
    }

    public static Meal dumplingsWithoutId() {
        var meal = new Meal();
        meal.setName("Dumplings");
        meal.setDescription("Pierogi – ogólnie: kawałki cienkiego, elastycznego i dobrze zlepiającego się ciasta " +
                "napełnione najrozmaitszymi farszami i ugotowane w wodzie lub na parze, upieczone, usmażone czy grillowane"
        );
        meal.setRecipe("Rób dobre pierogi, to cały przepis");
        meal.setImageUrl("https://www.kwestiasmaku.com/sites/kwestiasmaku.com/files/img_1013_0.jpg");
        return meal;
    }

    public static MealDto dumplingsWithIdDto() {
        var meal = dumplingsWithoutIdDto();
        meal.setId(DUMPLINGS_ID);
        return meal;
    }

    public static MealDto dumplingsWithoutIdDto() {
        var meal = new MealDto();
        meal.setName("Dumplings");
        meal.setDescription("Pierogi – ogólnie: kawałki cienkiego, elastycznego i dobrze zlepiającego się ciasta " +
                "napełnione najrozmaitszymi farszami i ugotowane w wodzie lub na parze, upieczone, usmażone czy grillowane"
        );
        meal.setRecipe("Rób dobre pierogi, to cały przepis");
        meal.setImageUrl("https://www.kwestiasmaku.com/sites/kwestiasmaku.com/files/img_1013_0.jpg");
        return meal;
    }



}
