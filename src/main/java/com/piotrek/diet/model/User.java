package com.piotrek.diet.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Document
@ToString
public class User {

    @Id
    @NotNull
    private String id;

    @Indexed(unique = true)
    @NotNull
    private Long facebookId;

    @Indexed(unique = true)
    private String username;

    @Email
    @Indexed(unique = true)
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime lastVisit;

    private String firstName;
    private String lastName;
    private String pictureUrl;
    private String role;
}
