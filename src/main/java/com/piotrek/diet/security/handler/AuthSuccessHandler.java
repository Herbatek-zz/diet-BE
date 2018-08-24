package com.piotrek.diet.security.handler;

import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        LinkedHashMap userDetails = (LinkedHashMap) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();

        Long facebookId = Long.valueOf(userDetails.get("id").toString());
        User user = userService.findByFacebookId(facebookId).block();
        if (user == null) {
            var email = userDetails.get("email").toString();
            var firstName = userDetails.get("first_name").toString();
            var lastName = userDetails.get("last_name").toString();
            user = userService.createUser(facebookId, email, firstName, lastName);
        }
        user.setLastVisit(LocalDateTime.now());
        user = userService.save(user).block();

        setCookie(response, "id", user.getId());

        response.sendRedirect("http://localhost:3000");

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
