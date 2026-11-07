
package com.placelyt.placelyt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String recipientEmail, String token) {

        String verificationUrl = frontendBaseUrl
                + "/verify-email?token="
                + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(recipientEmail);
        message.setSubject("Verify your Placelyt recruiter account");

        message.setText(
                "Hello,\n\n"
                + "Thank you for registering as a recruiter on Placelyt.\n\n"
                + "Please verify your company email by opening this link:\n"
                + verificationUrl
                + "\n\n"
                + "This verification link expires in 30 minutes "
                + "and can only be used once.\n\n"
                + "Email verification does not automatically approve "
                + "your recruiter account. Admin approval is also required.\n\n"
                + "If you did not request this registration, "
                + "you can ignore this email.\n\n"
                + "Regards,\n"
                + "Placelyt Team"
        );

        mailSender.send(message);
    }
}
