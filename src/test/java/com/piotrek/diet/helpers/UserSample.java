package com.piotrek.diet.helpers;

import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Role;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserDto;
import com.piotrek.diet.user.enums.Sex;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSample {

    private final static String JOHN_ID = UUID.randomUUID().toString();
    private final static String BAILEY_ID = UUID.randomUUID().toString();

    public static User johnWithoutId() {
        var user = new User(1233211L, "CoreySanders@dayrep.com", "Corey", "Sanders");
        user.setRole(Role.ROLE_USER.name());
        user.setLastVisit(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("Brulty");
        user.setCaloriesPerDay(2500);
        user.setSex(Sex.MAN);
        user.setActivity(Activity.AVERAGE);
        return user;
    }

    public static User johnWithId() {
        var user = johnWithoutId();
        user.setId(JOHN_ID);
        return user;
    }

    public static UserDto johnWithIdDto() {
        var userDto = new UserDto();
        userDto.setEmail("CoreySanders@dayrep.com");
        userDto.setFirstName("Corey");
        userDto.setLastName("Sanders");
        userDto.setUsername("Brulty");
        userDto.setId(JOHN_ID);
        userDto.setCaloriesPerDay(2500);
        userDto.setSex(Sex.MAN);
        userDto.setActivity(Activity.AVERAGE);
        return userDto;
    }

    public static User baileyWithoutId() {
        var user = new User(40022840639L, "BaileyHancock@teleworm.us", "Bailey", "Hancock");
        user.setRole(Role.ROLE_USER.name());
        user.setUsername("Cougge1940");
        user.setLastVisit(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setCaloriesPerDay(2200);
        user.setSex(Sex.MAN);
        user.setActivity(Activity.LOW);
        return user;
    }

    public static User baileyWithId() {
        var user = baileyWithoutId();
        user.setId(BAILEY_ID);
        return user;
    }

    public static UserDto baileyWithIdDto() {
        var userDto = new UserDto();
        userDto.setUsername("Cougge1940");
        userDto.setEmail("BaileyHancock@teleworm.us");
        userDto.setFirstName("Bailey");
        userDto.setLastName("Hancock");
        userDto.setId(BAILEY_ID);
        userDto.setCaloriesPerDay(2200);
        userDto.setSex(Sex.MAN);
        userDto.setActivity(Activity.LOW);
        return userDto;
    }
}
