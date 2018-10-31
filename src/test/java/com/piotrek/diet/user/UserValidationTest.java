package com.piotrek.diet.user;

import com.piotrek.diet.helpers.PrincipalProvider;
import com.piotrek.diet.helpers.UserSample;
import com.piotrek.diet.helpers.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidationTest {

    private UserValidation userValidation = new UserValidation();
    private User user = UserSample.johnWithId();

    @Test
    void validateUserWithPrincipal_whenTheyHaveTheSameIt_thenDoNothing() {
        PrincipalProvider.provide(user.getId());
        userValidation.validateUserWithPrincipal(user.getId());
    }

    @Test
    void validateUserWithPrincipal_whenTheyHaveDifferent_thenThrowBadRequestException() {
        PrincipalProvider.provide(user.getId());
        assertThrows(BadRequestException.class, () -> userValidation.validateUserWithPrincipal("badUserId"));
    }
}