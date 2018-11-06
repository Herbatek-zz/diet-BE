package com.piotrek.diet.user;

import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Sex;
import org.springframework.stereotype.Component;

@Component
public class CaloriesCalculator {

    int calculateCaloriesPerDay(UserDto userDto) {
        if (userDto.getHeight() != 0 && userDto.getWeight() != 0 && userDto.getAge() != 0
                && userDto.getSex() != null && userDto.getActivity() != null) {
            int bmr = calculateBMR(userDto.getWeight(), userDto.getHeight(), userDto.getAge(), userDto.getSex());
            return calculateTRM(bmr, userDto.getActivity());
        }
        return 0;
    }

    // BMR - basal metabolic rate
    private int calculateBMR(int weight, int height, int age, Sex sex) {
        return (int) Math.round((9.99 * weight) + (6.25 * height) + (4.92 * age) + sex.getValue());
    }

    // TRM - total metabolic rate
    private int calculateTRM(int bmr, Activity activity) {
        return (int) Math.round(bmr * activity.getValue());
    }
}
