package com.pcbuilder.core.modules.auth.utils;

import com.pcbuilder.core.modules.exception.EmailSendException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

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
    public void sendHtmlEmail(String to, String subject, String templateName,
                              Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(
                    "emails/" + templateName,
                    context
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("HTML email sent to: {} using template: {}", to, templateName);
        } catch (Exception e) {
            log.error("Failed to send HTML email", e);
            throw new EmailSendException("Failed to send HTML email");
        }
    }
    @Async
    public void sendHtmlEmailAsync(String to, String subject, String templateName,
                              Map<String, Object> variables) {
        sendHtmlEmail(to, subject, templateName, variables);
    }
}
