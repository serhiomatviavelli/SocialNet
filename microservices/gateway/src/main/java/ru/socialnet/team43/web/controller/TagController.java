package ru.socialnet.team43.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.socialnet.team43.client.CommunicationClient;
import ru.socialnet.team43.dto.TagDto;
import ru.socialnet.team43.util.ControllerUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tag")
public class TagController {

    private final CommunicationClient communicationClient;
    private final ControllerUtil controllerUtil;

    @GetMapping
    public ResponseEntity<List<TagDto>> getByName(@RequestParam String name) {
        ResponseEntity<List<TagDto>> responseEntity = communicationClient.getByName(name);
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

}
