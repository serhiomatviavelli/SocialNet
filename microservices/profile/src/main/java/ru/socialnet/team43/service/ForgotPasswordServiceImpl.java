package ru.socialnet.team43.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.model.EmailToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final EmailService emailService;
    private final DatabaseClient databaseClient;


    private Map<String, EmailToken> storageToken = new HashMap<>();

    @Value("${emailToken.lifeTime}")
    private long tokenLifeTime;

    @Value("${frontend.url}")
    private String hostUrl;

    @Override
    public void sendMailWithRecoveryLink(String email) throws MessagingException {
        EmailToken emailToken = createToken(email);
        storageToken.put(emailToken.getToken(), emailToken);
        String recoveryLink = createLink(emailToken.getToken());

        String content = "<!DOCTYPE HTML> <html>\n" +
                " <head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                " </head>\n" +
                " <body>\n" +"<p>Ссылка действительна в течении 5 минут</p>\n" +
                "  <a href=\""+ recoveryLink + "\">Нажмите для восстановления пароля</a>\n" +
                " </body>\n" +
                "</html>";

        emailService.sendMessageWithTemplate(email,content);
    }

    @Override
    public boolean setNewPassword(String token, String password) {
        EmailToken emailToken = storageToken.get(token);

        if (emailToken == null){
            log.info("token is null!");
            return false;
        }

        if (isExpired(emailToken)){
            log.info("token expired!");
            storageToken.remove(token);
            return false;
        }
        storageToken.remove(token);
        String email = emailToken.getEmail();

        HttpStatusCode statusCode = databaseClient.setNewPassword(password, email).getStatusCode();

        return statusCode.is2xxSuccessful();
    }

    @Override
    public String generatedToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime expireTimeRange() {
        return LocalDateTime.now().plusMinutes(tokenLifeTime);
    }

    @Override
    public boolean isExpired(EmailToken token) {
        LocalDateTime expireTime = token.getExpiredTime();
        return LocalDateTime.now().isAfter(expireTime);
    }

    @Override
    public EmailToken createToken(String email) {

        EmailToken token = new EmailToken();
        token.setToken(generatedToken());
        token.setExpiredTime(expireTimeRange());
        token.setEmail(email);

        return token;
    }

    @Override
    public String createLink(String token) {
        return hostUrl + "/change-password/" + token;
    }

    public Map<String, EmailToken> getEmailTokens(){
        return storageToken;
    }

}
