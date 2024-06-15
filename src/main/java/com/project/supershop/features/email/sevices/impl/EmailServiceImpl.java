package com.project.supershop.features.email.sevices.impl;

import com.project.supershop.features.email.sevices.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendSimpleMailMessage(String name, String to, String token) {

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
