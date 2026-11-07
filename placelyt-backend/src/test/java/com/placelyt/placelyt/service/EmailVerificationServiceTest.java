
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.exception.InvalidEmailVerificationTokenException;
import com.placelyt.placelyt.repository.UserRepository;
import com.placelyt.placelyt.util.EmailVerificationTokenUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailVerificationServiceTest {

    private UserRepository userRepository;
    private EmailVerificationService verificationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        verificationService =
                new EmailVerificationService(userRepository);
    }

    @Test
    void validTokenIsSuccessfullyConsumed() {
        String rawToken =
                EmailVerificationTokenUtils.generateToken();

        String expectedHash =
                EmailVerificationTokenUtils.hashToken(rawToken);

        when(userRepository.consumeEmailVerificationToken(
                eq(expectedHash),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(1);

        assertDoesNotThrow(
                () -> verificationService.verifyEmail(rawToken)
        );

        verify(userRepository).consumeEmailVerificationToken(
                eq(expectedHash),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void invalidTokenIsRejected() {
        String rawToken = "invalid-token";

        when(userRepository.consumeEmailVerificationToken(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(0);

        assertThrows(
                InvalidEmailVerificationTokenException.class,
                () -> verificationService.verifyEmail(rawToken)
        );
    }

    @Test
    void expiredTokenIsRejected() {
        String rawToken =
                EmailVerificationTokenUtils.generateToken();

        when(userRepository.consumeEmailVerificationToken(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(0);

        assertThrows(
                InvalidEmailVerificationTokenException.class,
                () -> verificationService.verifyEmail(rawToken)
        );
    }

    @Test
    void alreadyUsedTokenIsRejected() {
        String rawToken =
                EmailVerificationTokenUtils.generateToken();

        when(userRepository.consumeEmailVerificationToken(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(0);

        assertThrows(
                InvalidEmailVerificationTokenException.class,
                () -> verificationService.verifyEmail(rawToken)
        );
    }

    @Test
    void nullTokenIsRejected() {
        assertThrows(
                InvalidEmailVerificationTokenException.class,
                () -> verificationService.verifyEmail(null)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void blankTokenIsRejected() {
        assertThrows(
                InvalidEmailVerificationTokenException.class,
                () -> verificationService.verifyEmail("   ")
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void successfulVerificationConsumesTokenOnlyOnce() {
        String rawToken =
                EmailVerificationTokenUtils.generateToken();

        when(userRepository.consumeEmailVerificationToken(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(1).thenReturn(0);

        assertDoesNotThrow(
                () -> verificationService.verifyEmail(rawToken)
        );

        assertThrows(
                InvalidEmailVerificationTokenException.class,
                () -> verificationService.verifyEmail(rawToken)
        );

        verify(userRepository, times(2))
                .consumeEmailVerificationToken(
                        anyString(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                );
    }
}
