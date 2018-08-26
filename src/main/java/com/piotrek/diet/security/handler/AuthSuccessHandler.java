package com.piotrek.diet.security.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.piotrek.diet.security.token.Token;
import com.piotrek.diet.security.token.TokenService;
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

import static com.piotrek.diet.security.helpers.SecurityConstants.*;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        System.out.println("Siemanko jestem w SuccessfulHandler");

        LinkedHashMap userDetails = (LinkedHashMap) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();

        long facebookId = Long.valueOf(userDetails.get("id").toString());
        User user = userService.findByFacebookId(facebookId).block();
        Token token;
        if (user == null) {
            System.out.println("Tworzymy nowego usera");
            user = createUser(userDetails);
            System.out.println("Stworzyliśmy nowego usera: " + user.getUsername());
            user = userService.save(user).block();
            System.out.println("Zapisaliśmy usera: " + user.getUsername());

            System.out.println("Tworzymy nowy token dla usera: " + user.getUsername());
            token = new Token(tokenService.generateToken(user), user.getId());
            token = tokenService.save(token).block();
            System.out.println("Stworzyliśmy i zapisaliśmy token dla usera: " + user.getUsername());
        } else {
            System.out.println("Zalogował się istniejący usere: " + user.getUsername());
            token = tokenService.findByUserId(user.getId()).block();
            System.out.println("Teraz sprawdzimy token usera: " + user.getUsername());
            try {
                JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token.getToken().replace(TOKEN_PREFIX, ""));
            } catch (JWTVerificationException e) {
                System.out.println("Niestety podczas sprawdzania wystąpił problem, wygenerujemy nowy token dla usera: " + user.getUsername());
                token = tokenService.update(tokenService.generateToken(user), token.getId()).block();
            }
        }
        user.setLastVisit(LocalDateTime.now());
        user = userService.save(user).block();

        System.out.println("Dodawanie tokena do nagłówka");
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        setCookie(response, "id", user.getId());

//        response.sendRedirect("http://localhost:3000");

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private User createUser(LinkedHashMap userDetails) {
        String email = userDetails.get("email").toString();
        String firstName = userDetails.get("first_name").toString();
        String lastName = userDetails.get("last_name").toString();
        long facebookId = Long.valueOf(userDetails.get("id").toString());
        return new User(facebookId, email, firstName, lastName);
    }
}
