
package com.placelyt.placelyt.config;

import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class AdminProvisioningRunner implements ApplicationRunner {

    private static final String ADMIN_ROLE = "ADMIN";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${PLACELYT_ADMIN_PROVISION_ENABLED:false}")
    private boolean provisioningEnabled;

    @Value("${PLACELYT_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${PLACELYT_ADMIN_PASSWORD:}")
    private String adminPassword;

    public AdminProvisioningRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (!provisioningEnabled) {
            return;
        }

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN provisioning is enabled, but credentials are missing"
            );
        }

        String normalizedEmail = adminEmail.trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByRole(ADMIN_ROLE)) {
            System.out.println(
                    "ADMIN provisioning skipped: an ADMIN account already exists."
            );
            return;
        }

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalStateException(
                    "The configured ADMIN email already belongs to another account"
            );
        }

        if (adminPassword.length() < 12) {
            throw new IllegalStateException(
                    "Initial ADMIN password must contain at least 12 characters"
            );
        }

        User admin = new User();

        admin.setName("Placelyt Administrator");
        admin.setEmail(normalizedEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(ADMIN_ROLE);

        userRepository.save(admin);

        System.out.println(
                "Initial Placelyt ADMIN account provisioned successfully."
        );
        System.out.println(
                "Disable PLACELYT_ADMIN_PROVISION_ENABLED after provisioning."
        );
    }
}
