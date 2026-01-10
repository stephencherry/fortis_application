package com.the_olujare.fortis.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender javaMailSender;

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
}