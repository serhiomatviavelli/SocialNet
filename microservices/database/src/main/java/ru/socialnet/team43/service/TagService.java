package ru.socialnet.team43.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.TagDto;
import ru.socialnet.team43.repository.TagRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class TagService {

    private TagRepository tagRepository;

    public ResponseEntity<List<TagDto>> getByName(String name) {
        return ResponseEntity.ok().body(tagRepository.getByName(name));
    }

}
