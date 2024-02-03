package ru.socialnet.team43.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.socialnet.team43.configuration.MailProperties;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties properties;

    @Override
    public void sendMessageWithTemplate(String to, String content) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(properties.getSubject());
        mimeMessageHelper.setFrom(properties.getUserName());
        mimeMessageHelper.setText(content, true);
        javaMailSender.send(message);

    }


}
