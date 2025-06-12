package com.authentication.authentication.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.authentication.authentication.DTOs.EmailRequset;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleEmail(EmailRequset emailRequset) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailRequset.getTo());
        simpleMailMessage.setSubject(emailRequset.getSubject());
        simpleMailMessage.setText(emailRequset.getText());
        javaMailSender.send(simpleMailMessage);
    }
}
