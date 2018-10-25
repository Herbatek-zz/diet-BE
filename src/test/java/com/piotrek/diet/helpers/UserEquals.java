package com.piotrek.diet.helpers;

import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserDto;

public class UserEquals {

    public static boolean userEquals(User firstUser, User secondUser) {
        if (!firstUser.getId().equals(secondUser.getId()))
            return false;
        if (firstUser.getFacebookId() != secondUser.getFacebookId())
            return false;
        if (!firstUser.getUsername().equals(secondUser.getUsername()))
            return false;
        if (!firstUser.getEmail().equals(secondUser.getEmail()))
            return false;
        if (!firstUser.getFirstName().equals(secondUser.getFirstName()))
            return false;
        if (!firstUser.getLastName().equals(secondUser.getLastName()))
            return false;
        if (!firstUser.getPictureUrl().equals(secondUser.getPictureUrl()))
            return false;
        if (firstUser.getAge() != secondUser.getAge())
            return false;
        if (firstUser.getHeight() != secondUser.getHeight())
            return false;
        if (firstUser.getWeight() != secondUser.getWeight())
            return false;
        if (!firstUser.getCreatedAt().equals(secondUser.getCreatedAt()))
            return false;
        if (!firstUser.getLastVisit().equals(secondUser.getLastVisit()))
            return false;
        if (!firstUser.getRole().equals(secondUser.getRole()))
            return false;
        if (!firstUser.getFavouriteMeals().equals(secondUser.getFavouriteMeals()))
            return false;
        return true;
    }

    public static boolean userDtoEquals(UserDto firstUserDto, UserDto secondUserDto) {
        if (!firstUserDto.getId().equals(secondUserDto.getId()))
            return false;
        if (!firstUserDto.getUsername().equals(secondUserDto.getUsername()))
            return false;
        if (!firstUserDto.getEmail().equals(secondUserDto.getEmail()))
            return false;
        if (!firstUserDto.getFirstName().equals(secondUserDto.getFirstName()))
            return false;
        if (!firstUserDto.getLastName().equals(secondUserDto.getLastName()))
            return false;
        if (!firstUserDto.getPicture_url().equals(secondUserDto.getPicture_url()))
            return false;
        if (firstUserDto.getAge() != secondUserDto.getAge())
            return false;
        if (firstUserDto.getHeight() != secondUserDto.getHeight())
            return false;
        if (firstUserDto.getWeight() != secondUserDto.getWeight())
            return false;
        return true;
    }
}
