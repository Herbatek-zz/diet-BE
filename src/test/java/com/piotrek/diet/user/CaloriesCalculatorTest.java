package com.piotrek.diet.user;

import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Sex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaloriesCalculatorTest {

    private CaloriesCalculator calculator = new CaloriesCalculator();

    @Test
    void calculateCaloriesPerDay_whenManAndAverageActivity_thenCalculate() {
        UserDto user = UserSample.johnDto();
        user.setAge(23);
        user.setWeight(81);
        user.setHeight(175);
        user.setActivity(Activity.AVERAGE);
        user.setSex(Sex.MAN);
        user.setCaloriesPerDay(2781);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(user.getCaloriesPerDay(), actualCalories);
    }

    @Test
    void calculateCaloriesPerDay_whenNoAge_thenReturn0() {
        final int EXPECTED = 0;
        UserDto user = UserSample.johnDto();
        user.setWeight(81);
        user.setAge(0);
        user.setHeight(175);
        user.setActivity(Activity.AVERAGE);
        user.setSex(Sex.MAN);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(EXPECTED, actualCalories);
    }

    @Test
    void calculateCaloriesPerDay_whenNoWeight_thenReturn0() {
        final int EXPECTED = 0;
        UserDto user = UserSample.johnDto();
        user.setWeight(0);
        user.setAge(23);
        user.setHeight(175);
        user.setActivity(Activity.AVERAGE);
        user.setSex(Sex.MAN);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(EXPECTED, actualCalories);
    }

    @Test
    void calculateCaloriesPerDay_whenNoHeight_thenReturn0() {
        final int EXPECTED = 0;
        UserDto user = UserSample.johnDto();
        user.setWeight(81);
        user.setAge(23);
        user.setHeight(0);
        user.setActivity(Activity.AVERAGE);
        user.setSex(Sex.MAN);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(EXPECTED, actualCalories);
    }

    @Test
    void calculateCaloriesPerDay_whenNoSex_thenReturn0() {
        final int EXPECTED = 0;
        UserDto user = UserSample.johnDto();
        user.setWeight(81);
        user.setAge(23);
        user.setHeight(175);
        user.setActivity(Activity.AVERAGE);
        user.setSex(null);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(EXPECTED, actualCalories);
    }

    @Test
    void calculateCaloriesPerDay_whenNoActivity_thenReturn0() {
        final int EXPECTED = 0;
        UserDto user = UserSample.johnDto();
        user.setWeight(81);
        user.setAge(23);
        user.setHeight(175);
        user.setActivity(null);
        user.setSex(Sex.MAN);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(EXPECTED, actualCalories);
    }

    @Test
    void calculateCaloriesPerDay_whenWomanAndAverageActivity_thenCalculate() {
        UserDto user = UserSample.johnDto();
        user.setAge(45);
        user.setWeight(101);
        user.setHeight(169);
        user.setActivity(Activity.LOW);
        user.setSex(Sex.WOMAN);
        user.setCaloriesPerDay(2268);

        int actualCalories = calculator.calculateCaloriesPerDay(user);

        assertEquals(user.getCaloriesPerDay(), actualCalories);
    }
}