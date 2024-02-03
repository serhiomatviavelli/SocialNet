package ru.socialnet.team43.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public interface EmailService {

    void sendMessageWithTemplate(String to, String content) throws MessagingException;
}
