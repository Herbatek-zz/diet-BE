package com.piotrek.diet.model.dto.converter;

import com.piotrek.diet.model.User;
import com.piotrek.diet.model.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public UserDto toDto(User user) {
        var userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPicture_url(user.getPictureUrl());
        userDto.setRole(user.getRole());
        return userDto;
    }

    public User fromDto(UserDto userDto) {
        var user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setPictureUrl(userDto.getPicture_url());
        user.setRole(userDto.getRole());
        return user;
    }
}
