package com.authentication.authentication.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
