
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.exception.InvalidEmailVerificationTokenException;
import com.placelyt.placelyt.repository.UserRepository;
import com.placelyt.placelyt.util.EmailVerificationTokenUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailVerificationService {

    private final UserRepository userRepository;

    public EmailVerificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void verifyEmail(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidEmailVerificationTokenException(
                    "Verification token is required"
            );
        }

        String tokenHash;

        try {
            tokenHash = EmailVerificationTokenUtils.hashToken(rawToken);
        } catch (IllegalArgumentException exception) {
            throw new InvalidEmailVerificationTokenException(
                    "Invalid or expired verification link"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        int updatedRows = userRepository.consumeEmailVerificationToken(
                tokenHash,
                now,
                now
        );

        if (updatedRows != 1) {
            throw new InvalidEmailVerificationTokenException(
                    "Invalid, expired, or already-used verification link"
            );
        }
    }
}
