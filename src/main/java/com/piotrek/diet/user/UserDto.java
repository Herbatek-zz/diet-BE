package com.piotrek.diet.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = {"id"})
public class UserDto {

    private String id;

    @NotNull
    private String username;

    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String picture_url;

    private int age;

    private int height;

    private double weight;
}
