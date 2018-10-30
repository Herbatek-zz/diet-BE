package com.piotrek.diet.security.helpers;

public class SecurityConstants {

    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days in milliseconds
    public static final int COOKIE_MAX_AGE = (int) EXPIRATION_TIME / 10; // 10 days in seconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_IN_URL = "/login/facebook";
}
