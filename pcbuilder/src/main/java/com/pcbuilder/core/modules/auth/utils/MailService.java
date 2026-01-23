package com.pcbuilder.core.modules.auth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendTestEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Test Email from PCBuilder");
        message.setText("This is a test email sent from the PCBuilder application.");
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link: " + resetLink +
                        "\nThis link will expire in 24 hours.");
        mailSender.send(message);
    }

    public void sendEmailVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("To verify your email address, click the following link: " + verificationLink +
                        "\nThis link will expire in 60 minutes.");
        mailSender.send(message);
    }
}
