package com.piotrek.diet.user;

import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoConverterTest {

    private UserDtoConverter userDtoConverter = new UserDtoConverter();

    private User user = UserSample.johnWithId();
    private UserDto userDto = UserSample.johnWithIdDto();

    @Test
    void toDto() {
        var convertedUser = userDtoConverter.toDto(user);

        assertAll(
                () -> assertEquals(user.getFirstName(), convertedUser.getFirstName()),
                () -> assertEquals(user.getLastName(), convertedUser.getLastName()),
                () -> assertEquals(user.getId(), convertedUser.getId()),
                () -> assertEquals(user.getEmail(), convertedUser.getEmail()),
                () -> assertEquals(user.getUsername(), convertedUser.getUsername()),
                () -> assertEquals(user.getPictureUrl(), convertedUser.getPicture_url()),
                () -> assertEquals(user.getAge(), convertedUser.getAge()),
                () -> assertEquals(user.getHeight(), convertedUser.getHeight()),
                () -> assertEquals(user.getWeight(), convertedUser.getWeight())
        );
    }

    @Test
    void fromDto() {
        var convertedUser = userDtoConverter.fromDto(userDto);

        assertAll(
                () -> assertEquals(userDto.getFirstName(), convertedUser.getFirstName()),
                () -> assertEquals(userDto.getLastName(), convertedUser.getLastName()),
                () -> assertEquals(userDto.getId(), convertedUser.getId()),
                () -> assertEquals(userDto.getEmail(), convertedUser.getEmail()),
                () -> assertEquals(userDto.getUsername(), convertedUser.getUsername()),
                () -> assertEquals(userDto.getPicture_url(), convertedUser.getPictureUrl()),
                () -> assertEquals(userDto.getAge(), convertedUser.getAge()),
                () -> assertEquals(userDto.getHeight(), convertedUser.getHeight()),
                () -> assertEquals(userDto.getWeight(), convertedUser.getWeight())
        );
    }
}