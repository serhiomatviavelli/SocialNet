package ru.socialnet.team43;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommunicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunicationApplication.class, args);
	}
}
