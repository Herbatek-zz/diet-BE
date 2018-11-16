package com.piotrek.diet.helpers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public abstract class BaseDto implements Serializable {

    private String id;

    public BaseDto(String id) {
        this.id = id;
    }
}
