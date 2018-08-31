package com.piotrek.diet.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.piotrek.diet.sample.UserSample;
import com.piotrek.diet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.piotrek.diet.security.helpers.SecurityConstants.SECRET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    private Token token;
    private User user = UserSample.johnWithId();

    @BeforeEach
    private void beforeAll() {
        createToken();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findByToken() {
        Mockito.when(tokenRepository.findByToken(token.getToken())).thenReturn(Mono.just(token));

        Token byToken = tokenService.findByToken(token.getToken()).block();

        assertNotNull(byToken);
        assertAll(
                () -> assertEquals(token.getToken(), byToken.getToken()),
                () -> assertEquals(token.getUserId(), byToken.getUserId()),
                () -> assertEquals(token.getId(), byToken.getId())
        );

        verify(tokenRepository, times(1)).findByToken(token.getToken());
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void findByUserId() {
        Mockito.when(tokenRepository.findByUserId(token.getUserId())).thenReturn(Mono.just(token));

        Token byUserId = tokenService.findByUserId(token.getUserId()).block();

        assertNotNull(byUserId);
        assertAll(
                () -> assertEquals(token.getToken(), byUserId.getToken()),
                () -> assertEquals(token.getUserId(), byUserId.getUserId()),
                () -> assertEquals(token.getId(), byUserId.getId())
        );

        verify(tokenRepository, times(1)).findByUserId(token.getUserId());
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void save() {
        Mockito.when(tokenRepository.save(token)).thenReturn(Mono.just(token));

        Token saved = tokenService.save(token).block();

        assertNotNull(saved);
        assertAll(
                () -> assertEquals(token.getToken(), saved.getToken()),
                () -> assertEquals(token.getUserId(), saved.getUserId()),
                () -> assertEquals(token.getId(), saved.getId())
        );

        verify(tokenRepository, times(1)).save(token);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void update() {
        Mockito.when(tokenRepository.findById(token.getId())).thenReturn(Mono.just(token));
        Mockito.when(tokenRepository.save(token)).thenReturn(Mono.just(token));

        Token updated = tokenService.update(token.getToken(), token.getId()).block();

        assertNotNull(updated);
        assertAll(
                () -> assertEquals(token.getToken(), updated.getToken()),
                () -> assertEquals(token.getUserId(), updated.getUserId()),
                () -> assertEquals(token.getId(), updated.getId())
        );

        verify(tokenRepository, times(1)).findById(token.getId());
        verify(tokenRepository, times(1)).save(token);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void generateToken() {
        String tokenValue = tokenService.generateToken(user);

        DecodedJWT decodedToken = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(tokenValue);

        assertAll(
                () -> assertEquals(user.getId(), decodedToken.getSubject()),
                () -> assertEquals(user.getUsername(), decodedToken.getClaim("username").asString()),
                () -> assertEquals(user.getPictureUrl(), decodedToken.getClaim("pictureUrl").asString())
        );
    }

    private void createToken() {
        token = new Token("this.is.jwtToken", UUID.randomUUID().toString());
        token.setId(UUID.randomUUID().toString());
    }
}