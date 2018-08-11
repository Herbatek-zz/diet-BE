package com.piotrek.diet.validator;

import com.piotrek.diet.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public boolean isUserHasFirstName(String [] firstAndLastName) {
        return isFirstAndLastNameNotNull(firstAndLastName) && isFirstAndLastNameNotEmpty(firstAndLastName);
    }

    public boolean isUserHasLastName(String[] firstAndLastName) {
        return isFirstAndLastNameNotNull(firstAndLastName) && firstAndLastName.length >= 2;
    }

    public void checkFacebookId(Long facebookId) {
        if(facebookId == null)
            throw new BadRequestException("Cannot create user without facebook id");
    }

    private boolean isFirstAndLastNameNotNull(String[] firstAndLastName) {
        return firstAndLastName != null;
    }

    private boolean isFirstAndLastNameNotEmpty(String[] firstAndLastName) {
        return firstAndLastName.length != 0;
    }
}
