package com.piotrek.diet.user;

import com.piotrek.diet.helpers.exceptions.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public void validateUserWithPrincipal(String userId) {
        String principalId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (!userId.equals(principalId))
            throw new BadRequestException("You cannot save the product, because your id does not match with id from your token");
    }
}
