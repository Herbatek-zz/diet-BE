package com.piotrek.diet.user;

import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Role;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.user.enums.Sex;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
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

    private Sex sex;

    private Activity activity;

    private int age;

    private int height;

    private int weight;

    private int caloriesPerDay;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime lastVisit;

    @NotNull
    private String role;

    private HashSet<Meal> favouriteMeals = new HashSet<>();

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