package com.piotrek.diet.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = {"id"})
public class UserDto {

    private String id;

    @NotNull
    @Size(max = 15)
    private String username;

    @Email
    @Size(max = 254)
    private String email;

    @NotNull
    @Size(max = 35)
    private String firstName;

    @NotNull
    @Size(max = 35)
    private String lastName;

    @NotNull
    private String picture_url;

    @Max(140)
    private int age;

    @Max(250)
    private int height;

    @Max(450)
    private double weight;
}
