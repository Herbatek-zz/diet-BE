package com.piotrek.diet.security.token;

import com.auth0.jwt.JWT;
import com.piotrek.diet.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.piotrek.diet.security.helpers.SecurityConstants.EXPIRATION_TIME;
import static com.piotrek.diet.security.helpers.SecurityConstants.SECRET;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public Mono<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Mono<Token> findByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    public Mono<Token> save(Token token) {
        return tokenRepository.save(token);
    }

    public Mono<Token> update(String tokenValue, String tokenId) {
        Token token = tokenRepository.findById(tokenId).block();
        token.setToken(tokenValue);
        return tokenRepository.save(token);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
    }

}
