package com.piotrek.diet.helpers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public abstract class BaseDto implements Serializable {

    private String id = UUID.randomUUID().toString();

    public BaseDto(String id) {
        this.id = id;
    }
}
