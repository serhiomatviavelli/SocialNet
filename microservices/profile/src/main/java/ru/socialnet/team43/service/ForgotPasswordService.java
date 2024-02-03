package ru.socialnet.team43.service;

import jakarta.mail.MessagingException;
import ru.socialnet.team43.model.EmailToken;

import java.time.LocalDateTime;

public interface ForgotPasswordService {

    String generatedToken();

    LocalDateTime expireTimeRange();

    boolean isExpired(EmailToken token);

    EmailToken createToken(String email);

    String createLink(String token);

    void sendMailWithRecoveryLink(String email) throws MessagingException;

    boolean setNewPassword(String token, String password);


}
