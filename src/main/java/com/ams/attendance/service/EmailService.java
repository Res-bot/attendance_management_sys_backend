package com.ams.attendance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties.Simple;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j 
public class EmailService {

    private final JavaMailSender mailSender;

    
    public void sendEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            
            helper.setSubject(subject);
            helper.setText(body, false); 
            
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Error building email message for recipient: {}", to, e);
        } catch (MailException e) {
            log.error("Error sending email to recipient: {}", to, e);
        }
    }


    public void sendEmails(String to, String subject, String body) {
       SimpleMailMessage mail= new SimpleMailMessage();
       try {
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(body);
        mailSender.send(mail);
        
       } catch (Exception e) {
        log.error(body, e);
       }
        
    }
}

