package com.piotrek.diet.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.piotrek.diet.security.token.Token;
import com.piotrek.diet.security.token.TokenService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.piotrek.diet.security.helpers.SecurityConstants.*;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserService userService;
    private TokenService tokenService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("Siemanko jestem w filtrze JwtAuthorizationFilter !");
        request.getHeaderNames().asIterator().forEachRemaining(System.out::println);
        String header = request.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            System.out.println("Albo header był nullem, albo nie zaczynał się od barera...");
            chain.doFilter(request, response);
        } else {

            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String tokenValue = request.getHeader(HEADER_STRING);
        if (tokenValue != null) {
            System.out.println("Znalazłem header: " + tokenValue);
            String userId;
            try {
                userId = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(tokenValue.replace(TOKEN_PREFIX, ""))
                        .getSubject();
            } catch (JWTVerificationException e) {
                System.out.println("Niestety token jest niepoprawny");
                return null;
            }
            if (userId != null) {
                Token token = tokenService.findByToken(tokenValue.replace(TOKEN_PREFIX, "")).block();
                if(token == null) {
                    System.out.println("Nie znaleziono takiego tokenu w bazie danych");
                    return null;
                }
                if (!token.getToken().equals(tokenValue.replace(TOKEN_PREFIX, ""))) {
                    System.out.println("Token from database is different than token in the header!");
                    return null;
                }
                User userFromDB = userService.findById(userId).block();
                if (userFromDB == null) {
                    System.out.println("User read from DB by id read from token doesen't exist!");
                    return null;
                }
                System.out.println("Udało się poprawnie zalogować");
                return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
            }
        }
        return null;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
