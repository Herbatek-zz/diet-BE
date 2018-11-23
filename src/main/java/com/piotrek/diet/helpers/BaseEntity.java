package com.piotrek.diet.helpers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public abstract class BaseEntity implements Serializable {

    @Id
    private String id;

    @NotNull
    private LocalDateTime createdAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public BaseEntity(String id) {
        this();
        this.id = id;
    }
}
