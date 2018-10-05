package com.piotrek.diet.security.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.piotrek.diet.security.token.Token;
import com.piotrek.diet.security.token.TokenService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.piotrek.diet.security.helpers.SecurityConstants.SECRET;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        LinkedHashMap userDetails = (LinkedHashMap) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();

        long facebookId = Long.valueOf(userDetails.get("id").toString());
        User user = userService.findByFacebookId(facebookId).block();

        Token token;
        if (user == null) {
            user = createUser(userDetails);
            user = userService.save(user).block();
            token = new Token(tokenService.generateToken(user), user.getId());
            token = tokenService.save(token).block();
            log.debug("New user '" + user.getUsername() + "' has been created with new token");
        } else {
            log.debug("User '" + user.getUsername() + "' has been successfully authenticated");
            token = tokenService.findByUserId(user.getId()).block();
            try {
                JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token.getToken());
            } catch (JWTVerificationException e) {
                token = tokenService.update(tokenService.generateToken(user), token.getId()).block();
                log.info("User '" + user.getUsername() + "' had expired token, so we have generated a new one");
            }
        }
        user.setLastVisit(LocalDateTime.now());
        userService.save(user).block();

        Cookie cookie = new Cookie("Token", token.getToken());
        cookie.setPath("/");
        response.addCookie(cookie);

        response.sendRedirect("http://localhost:3000");

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User createUser(LinkedHashMap userDetails) {
        String email = userDetails.get("email").toString();
        String firstName = userDetails.get("first_name").toString();
        String lastName = userDetails.get("last_name").toString();
        long facebookId = Long.valueOf(userDetails.get("id").toString());
        User user = new User(facebookId, email, firstName, lastName);
        user.setPictureUrl("https://api.adorable.io/avatars/75/" + email + ".png");
        return user;
    }
}
