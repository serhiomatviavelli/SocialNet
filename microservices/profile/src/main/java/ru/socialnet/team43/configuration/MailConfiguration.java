package ru.socialnet.team43.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfiguration {

    private final MailProperties properties;

    @Bean
    public JavaMailSender getJavaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(properties.getHost());
        mailSender.setPort(properties.getPort());
        mailSender.setUsername(properties.getUserName());
        mailSender.setPassword(properties.getPassword());

        Properties subProperties = mailSender.getJavaMailProperties();
        subProperties.put("mail.transport.protocol", properties.getProtocol());
        subProperties.put("mail.smtp.auth", properties.getSmtpAuth());
        subProperties.put("mail.smtp.starttls.enable", properties.getSmtpStartTlsEnable());
        subProperties.put("mail.debug", properties.getDebugEnable());

        return mailSender;
    }
}
