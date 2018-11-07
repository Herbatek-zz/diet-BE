package com.piotrek.diet.security.token;

import com.piotrek.diet.helpers.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Document
public class Token extends BaseEntity {

    @NotNull
    @Indexed(unique = true)
    private String token;

    @NotNull
    @Indexed(unique = true)
    private String userId;

    public Token(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }
}
