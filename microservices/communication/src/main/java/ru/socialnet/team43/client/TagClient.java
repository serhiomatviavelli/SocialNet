package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.TagDto;

import java.util.List;

@FeignClient(name = "databaseTagClient", dismiss404 = true, url = "${database.url}" + "/api/v1/tag")
public interface TagClient {

    @GetMapping
    ResponseEntity<List<TagDto>> getByName(@RequestParam String name);

}
