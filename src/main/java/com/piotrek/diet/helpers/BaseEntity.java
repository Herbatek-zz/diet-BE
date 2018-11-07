package com.piotrek.diet.helpers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public abstract class BaseEntity implements Serializable {

    @Id
    private String id;

    public BaseEntity(String id) {
        this.id = id;
    }
}
