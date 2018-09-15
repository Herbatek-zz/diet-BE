package com.piotrek.diet.sample;

import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;

import java.util.UUID;

public class MealSample {

    private static final String DUMPLINGS_ID = UUID.randomUUID().toString();
    private static final String COFFEE_ID = UUID.randomUUID().toString();

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
        meal.setUserId("123four");
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
        meal.setUserId("123four");
        return meal;
    }

    public static Meal coffeeWithId() {
        var meal = coffeeWithoutId();
        meal.setId(COFFEE_ID);
        return meal;
    }

    public static Meal coffeeWithoutId() {
        var meal = new Meal();
        meal.setName("Kawa z czekoladą");
        meal.setDescription("Kawa i czekolada. Czy istnieje lepsza kombinacja smakowa? To sposób na deserową małą czarną," +
                " która skutecznie dodaje ernergii oraz zaspokaja apetyt na słodycze. Ten zgrany duet można udoskonalić" +
                " szczyptą cynamonu, mlekiem migdałowym oraz odrobiną likieru amaretto."
        );
        meal.setRecipe("Rób tą kawę");
        meal.setImageUrl("https://www.elle.pl/uploads/media/default/0003/69/5-najlepszych-przepisow-na-kawe-fot-fotolia.jpeg");
        return meal;
    }



}
