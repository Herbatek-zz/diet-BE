package com.piotrek.diet.security.token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@ToString
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

    public Token() {
    }

    public Token(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return Objects.equals(id, token1.id) &&
                Objects.equals(token, token1.token) &&
                Objects.equals(userId, token1.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, userId);
    }
}
