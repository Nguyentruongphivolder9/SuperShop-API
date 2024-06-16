package com.project.supershop.features.email.sevices.impl;

import com.project.supershop.features.email.sevices.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    @Value("${VERIFY_EMAIL_HOST}")
    private String host;
    @Value("${EMAIL_SENDER}")
    private String fromEmail; // Sử dụng EMAIL_SENDER từ application.properties
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender _javaMailSender) {
        this.javaMailSender = _javaMailSender;
    }

    @Override
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail); // Sử dụng fromEmail từ application.properties
            message.setTo(to);
            message.setText("Working, HAHAHHAHAHAAHAHA");
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void sendMimeMesageWithAttchments(String name, String to, String token) {

    }

    @Override
    public void sendMimeMessageWithEmbededImages(String name, String to, String token) {

    }

    @Override
    public void sendMimeMessageWithEmbededFiles(String name, String to, String token) {

    }

    @Override
    public void sendHtmlEmail(String name, String to, String token) {

    }

    @Override
    public void sendHtmlEmailWithEmbededFiles(String name, String to, String token) {

    }

}
