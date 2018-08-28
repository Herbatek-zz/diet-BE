package com.piotrek.diet.user;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {

    private String id;

    private String username;

    @Email
    private String email;

    private String firstName;
    private String lastName;
    private String picture_url;
    private String role;
}
