package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.socialnet.team43.client.TagClient;
import ru.socialnet.team43.dto.TagDto;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/tag")
public class TagController {

    private TagClient tagClient;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAll(@RequestParam String name) {
        return tagClient.getByName(name);
    }

}
