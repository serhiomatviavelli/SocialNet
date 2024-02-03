package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.UserAuthDto;

@FeignClient(name = "databaseClient", dismiss404 = true, url = "${database.url}" + "/auth")
public interface DatabaseClient {
    @GetMapping("/user")
    UserAuthDto getUserByEmail(@RequestParam("email") String email);
}
