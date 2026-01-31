package com.the_olujare.fortis.util;

import com.the_olujare.fortis.config.AppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender javaMailSender;
    private final AppProperties appProperties;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException messagingException) {
            throw new RuntimeException("Failed to send email", messagingException);
        }
    }

    public String loadAndProcessTemplate(String templateName, Map<String, String> variables) {
        try {
            var resource = new ClassPathResource("templates/email/" + templateName);
            String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : variables.entrySet()) {
                htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return htmlContent;
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to load email template: " + templateName, ioException);
        }
    }

    // Async method for sending verification emails
    @Async
    public void sendVerificationEmailAsync(String email, String token) {
        try {
            log.info("Sending verification email to: {}", email);
            String verificationUrl = appProperties.frontendUrl() + "/auth/verify?token=" + token;
            String htmlContent = loadAndProcessTemplate(
                    "verification.html",
                    Map.of("verificationUrl", verificationUrl)
            );
            sendHtmlEmail(email, "Kindly verify Your Fortis Account", htmlContent);
            log.info("Verification email sent successfully to: {}", email);
        } catch (Exception exception) {
            log.error("Failed to send verification email to: {}", email, exception);
        }
    }

    // Async method for sending password reset emails
    @Async
    public void sendPasswordResetEmailAsync(String email, String token) {
        try {
            log.info("Sending password reset email to: {}", email);
            String resetUrl = appProperties.frontendUrl() + "/auth/reset-password?token=" + token;
            String htmlContent = loadAndProcessTemplate(
                    "password-reset.html",
                    Map.of("resetUrl", resetUrl)
            );
            sendHtmlEmail(email, "Password Reset Request", htmlContent);
            log.info("Password reset email sent successfully to: {}", email);
        } catch (Exception exception) {
            log.error("Failed to send password reset email to: {}", email, exception);
        }
    }
}