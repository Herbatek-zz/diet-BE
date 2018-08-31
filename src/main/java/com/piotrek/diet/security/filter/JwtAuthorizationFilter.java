package com.piotrek.diet.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.piotrek.diet.security.token.Token;
import com.piotrek.diet.security.token.TokenService;
import com.piotrek.diet.user.User;
import com.piotrek.diet.user.UserService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserService userService;
    private TokenService tokenService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String tokenValue = request.getHeader(HEADER_STRING);
        if (tokenValue == null) {
            log.debug(HEADER_STRING + " is null");
            chain.doFilter(request, response);
        } else if (!tokenValue.startsWith(TOKEN_PREFIX)) {
            log.warn(HEADER_STRING + " does not starts with '" + TOKEN_PREFIX + "'");
            chain.doFilter(request, response);
        } else {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String tokenValue = request.getHeader(HEADER_STRING);
        tokenValue = tokenValue.replace(TOKEN_PREFIX, "");
        String userId;
        try {
            userId = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(tokenValue)
                    .getSubject();
        } catch (JWTVerificationException e) {
            log.info("Token '" + tokenValue + "' is invalid");
            return null;
        }
        if (userId != null) {
            Token token = tokenService.findByToken(tokenValue).block();
            if (token == null) {
                log.warn("Not found token '" + tokenValue + "' in database");
                return null;
            }
            if (!token.getToken().equals(tokenValue)) {
                log.warn("Token from database is different than token from header!");
                return null;
            }
            User userFromDB = userService.findById(userId).block();
            if (userFromDB == null) {
                log.warn("User from token does not exist in database");
                return null;
            }
            log.info("User '" + userFromDB.getUsername() + "' has been authenticated");
            return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
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
