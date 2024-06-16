package com.project.supershop.features.email.sevices.impl;

import com.project.supershop.features.email.sevices.EmailService;
import com.project.supershop.utils.EmailUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    @Value("${spring.pulsar.client.service-url}")
    private String serverUrl;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail; // Sử dụng email.userName từ application.properties

    private final JavaMailSender javaMailSender;
    private final ResourceLoader resourceLoader;

    public EmailServiceImpl(JavaMailSender _javaMailSender, ResourceLoader resourceLoader) {
        this.javaMailSender = _javaMailSender;
        this.resourceLoader = resourceLoader;
    }

    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail); // Sử dụng fromEmail từ application.properties
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name, serverUrl, token));
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMesageWithAttchments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail); // Sử dụng fromEmail từ application.properties
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, serverUrl, token));
            //Add attachments

            // Đường dẫn tuyệt đối hoặc tương đối từ hệ thống tập tin
            String imagePath = "file:src/main/java/static/images/your-image.jpg";

            // Tạo Resource từ đường dẫn
            Resource resource = resourceLoader.getResource(imagePath);
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    @Async
    public void sendMimeMessageWithEmbededImages(String name, String to, String token) {

    }

    @Override
    @Async
    public void sendMimeMessageWithEmbededFiles(String name, String to, String token) {

    }

    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {

    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbededFiles(String name, String to, String token) {

    }


    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }


}
