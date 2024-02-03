package ru.socialnet.team43.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rating")
public class RatingProperties {
    private int max;
    private int min;
}
