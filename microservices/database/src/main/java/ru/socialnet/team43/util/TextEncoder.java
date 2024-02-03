package ru.socialnet.team43.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class TextEncoder {
    private final static String ENCRYPTION_PASSWORD;
    private final static String ENCRYPTION_SALT;
    private final static TextEncryptor encryptor;

    static {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yaml"));
        Properties properties = yaml.getObject();

        ENCRYPTION_PASSWORD = properties.getProperty("encryption.password");
        ENCRYPTION_SALT = properties.getProperty("encryption.salt");
        encryptor = Encryptors.text(ENCRYPTION_PASSWORD, ENCRYPTION_SALT);
    }

    public static String encryptMessage(String message) {
        return encryptor.encrypt(message);
    }

    public static String decryptMessage(String message) {
        return encryptor.decrypt(message);
    }
}
