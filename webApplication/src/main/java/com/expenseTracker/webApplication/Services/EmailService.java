package com.expenseTracker.webApplication.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }


    public void sendNotificationEmail(String email, String subject, String userName, String description) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject(subject);

        String content = getContentWithPlaceholders(userName,description);
        helper.setText(content, true);

        javaMailSender.send(message);
    }

        private String getContentWithPlaceholders( String userName, String description) throws IOException {
            String content = new String(new ClassPathResource("templates/emailTemplate.html").getInputStream().readAllBytes());
            content = content.replace("${userName}", userName)
                    .replace("${Description}", description);

            return content;
        }


}
