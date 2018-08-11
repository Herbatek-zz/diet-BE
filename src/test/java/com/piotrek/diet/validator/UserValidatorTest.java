package com.piotrek.diet.validator;

import com.piotrek.diet.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private UserValidator userValidator = new UserValidator();

    @Test
    void isUserHasFirstName_whenArrayWithTwoString_returnTrue() {
        String[] firstAndLast = {"first", "last"};

        assertTrue(userValidator.isUserHasFirstName(firstAndLast));
    }

    @Test
    void isUserHasFirstName_whenArrayWithOneString_returnFalse() {
        String[] firstAndLast = {"first"};

        assertTrue(userValidator.isUserHasFirstName(firstAndLast));
    }

    @Test
    void isUserHasFirstName_whenArrayWithEmptyArray_returnFalse() {
        String[] firstAndLast = {};

        assertFalse(userValidator.isUserHasFirstName(firstAndLast));
    }

    @Test
    void isUserHasFirstName_whenArrayWithNull_returnFalse() {
        assertFalse(userValidator.isUserHasFirstName(null));
    }

    @Test
    void isUserHasLastName_whenArrayWithTwoString_returnTrue() {
        String[] firstAndLast = {"first", "last"};

        assertTrue(userValidator.isUserHasLastName(firstAndLast));
    }

    @Test
    void isUserHasLastName_whenArrayWithOneString_returnFalse() {
        String[] firstAndLast = {"first"};

        assertFalse(userValidator.isUserHasLastName(firstAndLast));
    }

    @Test
    void isUserHasLastName_whenArrayWithEmptyArray_returnFalse() {
        String[] firstAndLast = {};

        assertFalse(userValidator.isUserHasLastName(firstAndLast));
    }

    @Test
    void isUserHasLastName_whenArrayWithNull_returnFalse() {
        assertFalse(userValidator.isUserHasLastName(null));
    }

    @Test
    void checkFacebookId_whenNull_thenThrowException() {
        assertThrows(BadRequestException.class, () -> userValidator.checkFacebookId(null));
    }

    @Test
    void checkFacebookId_whenLong_thenDoNothing() {
        assertDoesNotThrow(() -> userValidator.checkFacebookId(123131L));
    }
}