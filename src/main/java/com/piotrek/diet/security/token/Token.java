package com.piotrek.diet.security.token;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document
public class Token {

    @Id
    private String id;

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
