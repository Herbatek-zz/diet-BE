package com.piotrek.diet.config;

import com.piotrek.diet.model.User;
import com.piotrek.diet.model.enums.Role;
import com.piotrek.diet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);

        LinkedHashMap userDetails = (LinkedHashMap) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();

        Long facebookId = Long.valueOf(userDetails.get("id").toString());

        User user = userService.findByFacebookId(facebookId).block();
        if (user == null) {
            String[] name = ((String) userDetails.get("name")).split(" ");
            String firstName = name[0];
            String lastName = name[1];

            user = new User();
            user.setFacebookId(facebookId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(Role.ROLE_USER.name());

            userService.save(user).block();
        }
    }
}
