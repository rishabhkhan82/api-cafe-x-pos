package com.cafex.pos.service;

import com.cafex.pos.entity.EmailServiceConfigurations;
import com.cafex.pos.repository.EmailServiceConfigurationsRepository;
import com.cafex.pos.exception.ApiException;
import jakarta.mail.MessagingException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailServiceConfigurationsRepository configRepository;

    @Value("${app.mail.from.email:cafexpos@gmail.com}")
    private String defaultFromEmail;

    @Value("${app.mail.from.name:CafeX POS}")
    private String defaultFromName;

    @Value("${app.mail.enabled:true}")
    private Boolean mailEnabled;

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!Boolean.TRUE.equals(mailEnabled)) {
            log.info("Email sending is disabled. Skipping email to: {}", to);
            return;
        }

        try {
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }

            String htmlContent = templateEngine.process("emails/" + templateName, context);

            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                helper.setFrom(defaultFromEmail, defaultFromName);

                javaMailSender.send(mimeMessage);
            } catch (java.io.UnsupportedEncodingException e) {
                throw new MessagingException("Unsupported encoding", e);
            }
            log.info("Email sent successfully to: {} with template: {}", to, templateName);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {} with template: {}. Error: {}", to, templateName, e.getMessage(), e);
            throw new ApiException("INTERNAL_ERROR", "Failed to send email: " + e.getMessage(), 500);
        }
    }

    public void sendTextEmail(String to, String subject, String text) {
        if (!Boolean.TRUE.equals(mailEnabled)) {
            log.info("Email sending is disabled. Skipping email to: {}", to);
            return;
        }

        try {
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, false);
                helper.setFrom(defaultFromEmail, defaultFromName);

                javaMailSender.send(mimeMessage);
            } catch (java.io.UnsupportedEncodingException e) {
                throw new MessagingException("Unsupported encoding", e);
            }
            log.info("Text email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send text email to: {}. Error: {}", to, e.getMessage(), e);
            throw new ApiException("INTERNAL_ERROR", "Failed to send email: " + e.getMessage(), 500);
        }
    }

    public EmailServiceConfigurations getActiveConfiguration() {
        List<EmailServiceConfigurations> configs = configRepository.findAll();
        return configs.isEmpty() ? null : configs.get(0);
    }
}
