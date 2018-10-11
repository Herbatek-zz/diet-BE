package com.piotrek.diet.sample;

import com.piotrek.diet.helpers.enums.Role;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserDto;

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
        return user;
    }

    public static User johnWithId() {
        var user = johnWithoutId();
        user.setId(JOHN_ID);
        return user;
    }

    public static UserDto johnWithoutIdDto() {
        var userDto = new UserDto();
        userDto.setEmail("CoreySanders@dayrep.com");
        userDto.setFirstName("Corey");
        userDto.setLastName("Sanders");
        userDto.setUsername("Brulty");
        return userDto;
    }

    public static UserDto johnWithIdDto() {
        var userDto = johnWithoutIdDto();
        userDto.setId(johnWithId().getId());
        return userDto;
    }

    public static User baileyWithoutId() {
        var user = new User(40022840639L, "BaileyHancock@teleworm.us", "Bailey", "Hancock");
        user.setRole(Role.ROLE_USER.name());
        user.setUsername("Cougge1940");
        user.setLastVisit(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static User baileyWithId() {
        var user = baileyWithoutId();
        user.setId(BAILEY_ID);
        return user;
    }

    public static UserDto baileyWithoutIdDto() {
        var userDto = new UserDto();
        userDto.setUsername("Cougge1940");
        userDto.setEmail("BaileyHancock@teleworm.us");
        userDto.setFirstName("Bailey");
        userDto.setLastName("Hancock");
        return userDto;
    }

    public static UserDto baileyWithIdDto() {
        var userDto = baileyWithoutIdDto();
        userDto.setId(baileyWithId().getId());
        return userDto;
    }
}
