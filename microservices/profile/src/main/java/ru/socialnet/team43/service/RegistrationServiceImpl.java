package ru.socialnet.team43.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.RegDto;
import ru.socialnet.team43.dto.RegDtoDb;
import ru.socialnet.team43.dto.enums.Roles;
import ru.socialnet.team43.util.CaptchaCreator;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    @Value("${frontend.url}")
    private String hostUrl;
    private final DatabaseClient databaseClient;
    private final CaptchaCreator captchaCreator;

    private final EmailService emailService;
    private final static String REG_PROCESS = "Registration process: ";

    @Override
    public boolean registrationPerson(RegDto regDto) {
        String captchaCode = regDto.getCaptchaCode();
        String captchaSecret = regDto.getCaptchaSecret();
        String email = regDto.getEmail();

        if (email == null) {
            log.info(REG_PROCESS + "email is null");
            return false;
        }

        if (isCaptchaCorrect(captchaCode, captchaSecret)
                && !isEmailExisted(email)) {

            RegDtoDb regDtoForDb = createRegDtoForDb(regDto, Roles.USER);

            boolean resultRegistration = createPerson(regDtoForDb);

            if (resultRegistration){
                sendConfirmationLetter(email,hostUrl);
                return true;
            }
        }

        return false;
    }


    private boolean isCaptchaCorrect(String captchaCode, String captchaSecret) {
        if (captchaCode != null) {

            String captchaAnswer = captchaCreator.getCaptchaList().get(captchaSecret);

            if (captchaCode.equals(captchaAnswer)) {
                log.info(REG_PROCESS + "Captcha is correct");
                return true;
            }
        }
        log.info(REG_PROCESS + "Captcha is not correct");
        return false;
    }

    private boolean isEmailExisted(String email) {
        HttpStatusCode statusCode = databaseClient.isEmailExist(email).getStatusCode();

        if (statusCode.is2xxSuccessful()) {
            log.info(REG_PROCESS + "Person with email ({}) has already existed," +
                    "registration failed", email);
            return true;
        }
        log.info(REG_PROCESS + "email is unique");
        return false;
    }

    private void sendConfirmationLetter(String to, String link){

        String content = "<!DOCTYPE HTML> <html>\n" +
                " <head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                " </head>\n" +
                " <body>\n" +"<p>Поздравляем! Вы были успешно зарегистированы на сайте:</p>\n" +
                "  <a href=\""+ link + "\">Социальная сеть</a>\n" +
                " </body>\n" +
                "</html>";

        try {
            emailService.sendMessageWithTemplate(to,content);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    private boolean createPerson(RegDtoDb regDtoDb) {
        HttpStatusCode statusCode = databaseClient.createPerson(regDtoDb).getStatusCode();
        if (statusCode.is2xxSuccessful()) {
            log.info(REG_PROCESS + "Person has been registered");
            return true;
        }
        log.info(REG_PROCESS + "Person has not been registered, database does not answer");
        return false;
    }


    private RegDtoDb createRegDtoForDb(RegDto regDto, Roles role) {
        return RegDtoDb.builder()
                .email(regDto.getEmail())
                .password(regDto.getPassword1())
                .firstName(regDto.getFirstName())
                .lastName(regDto.getLastName())
                .role(role)
                .build();
    }


}
