package com.piotrek.diet.helpers;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalProvider {

    public static void provide(String userId) {
        var testingAuthentication = new TestingAuthenticationToken(userId, null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);
    }
}
