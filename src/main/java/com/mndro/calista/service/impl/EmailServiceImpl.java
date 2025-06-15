package com.mndro.calista.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;
//tmnp woqh whif oqjq

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl {
    private final JavaMailSender javaMailSender;
    public void sendEmail(String to, String subject, String text){
        log.info("send email to");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("ariniahlasalsabila@gmail.com");
        javaMailSender.send(message);
    }
    public void sendEmailWithAttachment(String to, String subject, String text, File file) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.setFrom("ariniahlasalsabila@gmail.com");
        helper.addAttachment(file.getName(), file);
        javaMailSender.send(message);
    }
}