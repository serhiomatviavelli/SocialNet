package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.UserAuthDto;

@FeignClient(name = "userAuth", dismiss404 = true, url = "${database.url}" + "/auth")
public interface UserAuthClient {

        @GetMapping("auth/user")
        UserAuthDto getUserByEmail(@RequestParam("email") String email);

        @GetMapping("/register/email")
        ResponseEntity<Void> isEmailExist(@RequestParam("email") String email);

        @PostMapping("/register/create")
        ResponseEntity<Void> createPerson(PersonDto personDto);
}
