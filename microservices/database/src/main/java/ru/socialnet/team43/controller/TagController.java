package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.TagDto;
import ru.socialnet.team43.service.TagService;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/api/v1/tag")
public class TagController {

    private TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAll(@RequestParam String name) {
        return tagService.getByName(name);
    }

}
