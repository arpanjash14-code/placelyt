
package com.placelyt.placelyt.util;

import java.net.IDN;
import java.util.Locale;

public final class EmailDomainUtils {

    private EmailDomainUtils() {
    }

    public static String extractDomain(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        int atIndex = normalizedEmail.lastIndexOf('@');

        if (atIndex <= 0 || atIndex == normalizedEmail.length() - 1) {
            throw new IllegalArgumentException("Invalid email address");
        }

        String domain = normalizedEmail.substring(atIndex + 1);

        try {
            return IDN.toASCII(domain).toLowerCase(Locale.ROOT);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid email domain");
        }
    }
}
