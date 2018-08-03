package com.piotrek.diet.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;


@Getter
@Setter
@Document
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String firstName;
    private String lastName;

    @Email
    @Indexed(unique = true)
    private String email;
    private String provider;
    private String picture;
    private boolean enabled;

    private String role;
}
