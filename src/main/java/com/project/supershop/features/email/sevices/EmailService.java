package com.project.supershop.features.email.sevices;

import org.springframework.stereotype.Component;

@Component
public interface EmailService {
    //Gửi 1 message đơn giản qua email
    void sendSimpleMailMessage(String name, String to, String token);

    void sendMimeMesageWithAttchments(String name, String to, String token);

    void sendMimeMessageWithEmbededImages(String name, String to, String token);

    void sendMimeMessageWithEmbededFiles(String name, String to, String token);

    void sendHtmlEmail(String name, String to, String token);

    void sendHtmlEmailWithEmbededFiles(String name, String to, String token);

}
