package com.piotrek.diet.user.enums;

import lombok.Getter;

@Getter
public enum Sex {
    MAN(5), WOMAN(-161);

    private int value;

    Sex(int value) {
        this.value = value;
    }
}
