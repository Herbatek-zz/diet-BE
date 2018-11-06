package com.piotrek.diet.user.enums;

import lombok.Getter;

@Getter
public enum Activity {
    VERY_LOW(1.2),LOW(1.35),AVERAGE(1.55),HIGH(1.75),VERY_HIGH(2.05);

    private double value;

    Activity(double value) {
        this.value = value;
    }

}
