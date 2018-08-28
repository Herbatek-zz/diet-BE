package com.piotrek.diet.sample;

import com.piotrek.diet.helpers.enums.Role;
import com.piotrek.diet.user.User;

public class UserSample {

    public static User johnWithId() {
        var user = new User();
        user.setFacebookId(12351L);
        user.setId("idSuchLong123");
        user.setFirstName("John");
        user.setLastName("TheGamer");
        user.setRole(Role.ROLE_USER.name());
        user.setEmail("JTG@email.com");
        user.setUsername("jtGAMER");
        return user;
    }

    public static User johnWithoutId() {
        var user = new User();
        user.setFacebookId(1233211L);
        user.setFirstName("Corey");
        user.setLastName("Sanders");
        user.setRole(Role.ROLE_USER.name());
        user.setEmail("CoreySanders@dayrep.com ");
        user.setUsername("Brulty");
        return user;
    }

    public static User baileyWithoutId() {
        var user = new User();
        user.setFacebookId(40022840639L);
        user.setFirstName("Bailey");
        user.setLastName("Hancock");
        user.setRole(Role.ROLE_USER.name());
        user.setEmail("BaileyHancock@teleworm.us ");
        user.setUsername("Cougge1940");
        return user;
    }
}
