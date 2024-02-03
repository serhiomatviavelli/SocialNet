package ru.socialnet.team43.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.CommunicationClient;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.PostDto;
import ru.socialnet.team43.util.ControllerUtil;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final CommunicationClient communicationClient;
    private final ProfileClient profileClient;
    private final ControllerUtil controllerUtil;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getAll(@RequestParam(required = false) List<Long> ids,
                                                @RequestParam(required = false) List<Long> accountIds,
                                                @RequestParam(required = false) List<Long> blockedIds,
                                                @RequestParam(required = false) String author,
                                                @RequestParam(required = false) String text,
                                                @RequestParam(defaultValue = "true") Boolean withFriends,
                                                @RequestParam(required = false) Boolean isBlocked,
                                                @RequestParam(defaultValue = "false") Boolean isDeleted,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo,
                                                @RequestParam(required = false) List<String> tags,
                                                @RequestParam(defaultValue = "time,desc") String sort,
                                                Pageable pageable) {
        ResponseEntity<Page<PostDto>> responseEntity = ResponseEntity.ok().body(communicationClient.getAll(ids, accountIds, blockedIds, author, text, withFriends, isBlocked, isDeleted, dateFrom, dateTo, tags, sort, pageable));
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        ResponseEntity<PostDto> responseEntity = communicationClient.getPostById(id);
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

    @PostMapping
    ResponseEntity<PostDto> addNewPost(@RequestBody PostDto postDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            PersonDto author = profileClient.getMyProfile(userDetails.getUsername()).getBody();
            assert author != null;
            postDto.setAuthorId(author.getId());
            ResponseEntity<PostDto> responseEntity = ResponseEntity.ok().body(communicationClient.addNewPost(postDto));
            return controllerUtil.createNewResponseEntity(responseEntity);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    ResponseEntity<Long> editPost(@RequestBody PostDto postDto) {
        ResponseEntity<Long> responseEntity = communicationClient.editPost(postDto);
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Long> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PersonDto author = profileClient.getMyProfile(userDetails.getUsername()).getBody();
        PostDto post = communicationClient.getPostById(id).getBody();
        assert author != null;
        assert post != null;
        if (Objects.equals(author.getId(), post.getAuthorId())) {
            ResponseEntity<Long> responseEntity = communicationClient.deletePost(id);
            return controllerUtil.createNewResponseEntity(responseEntity);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
