package ru.socialnet.team43.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    private String host;
    private int port;
    private String userName;
    private String password;
    private String protocol;
    private String smtpAuth;
    private String smtpStartTlsEnable;
    private String debugEnable;
    private String subject;
    private String mailText;
}
