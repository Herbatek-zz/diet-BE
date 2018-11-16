package com.piotrek.diet.user;

import com.piotrek.diet.helpers.BaseDto;
import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {

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

    private Sex sex;

    private Activity activity;

    @Max(140)
    private int age;

    @Max(250)
    private int height;

    @Max(450)
    private int weight;

    private int caloriesPerDay;

    private int proteinPerDay;

    private int carbohydratePerDay;

    private int fatPerDay;

    public UserDto(String id, String username, String email, String firstName, String lastName, String picture_url,
                   Sex sex, Activity activity, int age, int height, int weight, int caloriesPerDay) {
        super(id);
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture_url = picture_url;
        this.sex = sex;
        this.activity = activity;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.caloriesPerDay = caloriesPerDay;
    }
}
