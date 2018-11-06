package com.piotrek.diet.user;

import com.piotrek.diet.helpers.UserSample;
import org.junit.jupiter.api.Test;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertUserFields;

class UserDtoConverterTest {

    private UserDtoConverter userDtoConverter = new UserDtoConverter();

    private User user = UserSample.johnWithId();
    private UserDto userDto = UserSample.johnWithIdDto();

    @Test
    void toDto() {
        var convertedUser = userDtoConverter.toDto(user);
        assertUserFields(userDto, convertedUser);
    }

    @Test
    void fromDto() {
        var convertedUser = userDtoConverter.fromDto(userDto);
        assertUserFields(user, convertedUser);
    }
}