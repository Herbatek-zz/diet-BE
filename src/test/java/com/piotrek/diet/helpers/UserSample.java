package com.piotrek.diet.helpers;

import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserDto;
import com.piotrek.diet.user.enums.Activity;
import com.piotrek.diet.user.enums.Role;
import com.piotrek.diet.user.enums.Sex;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSample {

    private final static String JOHN_ID = UUID.randomUUID().toString();
    private final static Long JOHN_FACEBOOK_ID = 12345654321L;
    private final static String JOHN_USERNAME = "Brulty";
    private final static String JOHN_EMAIL = "CoreySanders@dayrep.com";
    private final static String JOHN_FIRST_NAME = "Corey";
    private final static String JOHN_LAST_NAME = "Sanders";
    private final static String JOHN_PICTURE_URL = "www.fancy-picture.com/brulty.jpg";
    private final static Sex JOHN_SEX = Sex.MAN;
    private final static Activity JOHN_ACTIVITY = Activity.AVERAGE;
    private final static int JOHN_AGE = 25;
    private final static int JOHN_HEIGHT = 178;
    private final static int JOHN_WEIGHT = 90;
    private final static int JOHN_CALORIES = 3000;
    private final static int JOHN_PROTEIN = 160;
    private final static int JOHN_CARBO = 340;
    private final static int JOHN_FAT = 95;

    private final static String BAILEY_ID = UUID.randomUUID().toString();
    private final static Long BAILEY_FACEBOOK_ID = 987654321332L;

    public static User john() {
        User user = new User();
        user.setId(JOHN_ID);
        user.setFacebookId(JOHN_FACEBOOK_ID);
        user.setUsername(JOHN_USERNAME);
        user.setEmail(JOHN_EMAIL);
        user.setFirstName(JOHN_FIRST_NAME);
        user.setLastName(JOHN_LAST_NAME);
        user.setPictureUrl(JOHN_PICTURE_URL);
        user.setSex(JOHN_SEX);
        user.setActivity(JOHN_ACTIVITY);
        user.setAge(JOHN_AGE);
        user.setHeight(JOHN_HEIGHT);
        user.setWeight(JOHN_WEIGHT);
        user.setCaloriesPerDay(JOHN_CALORIES);
        user.setProteinPerDay(JOHN_PROTEIN);
        user.setCarbohydratePerDay(JOHN_CARBO);
        user.setFatPerDay(JOHN_FAT);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastVisit(LocalDateTime.now());
        user.setRole(Role.ROLE_USER);
        return user;
    }

    public static UserDto johnDto() {
        UserDto user = new UserDto();
        user.setId(JOHN_ID);
        user.setUsername(JOHN_USERNAME);
        user.setEmail(JOHN_EMAIL);
        user.setFirstName(JOHN_FIRST_NAME);
        user.setLastName(JOHN_LAST_NAME);
        user.setPicture_url(JOHN_PICTURE_URL);
        user.setSex(JOHN_SEX);
        user.setActivity(JOHN_ACTIVITY);
        user.setAge(JOHN_AGE);
        user.setHeight(JOHN_HEIGHT);
        user.setWeight(JOHN_WEIGHT);
        user.setCaloriesPerDay(JOHN_CALORIES);
        user.setProteinPerDay(JOHN_PROTEIN);
        user.setCarbohydratePerDay(JOHN_CARBO);
        user.setFatPerDay(JOHN_FAT);
        return user;
    }

    public static User baileyWithId() {
        var user = new User(BAILEY_FACEBOOK_ID, "BaileyHancock@teleworm.us", "Bailey", "Hancock");
        user.setRole(Role.ROLE_USER);
        user.setUsername("Cougge1940");
        user.setLastVisit(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setCaloriesPerDay(2200);
        user.setSex(Sex.MAN);
        user.setActivity(Activity.LOW);
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
