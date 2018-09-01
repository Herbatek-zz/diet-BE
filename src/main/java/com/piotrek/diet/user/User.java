package com.piotrek.diet.user;

import com.piotrek.diet.helpers.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document
public class User {

    @Id
    @NotNull
    private String id;

    @NotNull
    @Indexed(unique = true)
    private long facebookId;

    @NotNull
    @Indexed(unique = true)
    private String username;

    @Email
    @NotNull
    @Indexed(unique = true)
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String pictureUrl;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime lastVisit;

    @NotNull
    private String role;

    public User() {}

    public User(long facebookId, String email, String firstName, String lastName) {
        this.facebookId = facebookId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = firstName + " " + lastName;
        this.role = Role.ROLE_USER.name();
        this.createdAt = LocalDateTime.now();
    }
}
