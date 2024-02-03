package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.PostDto;
import ru.socialnet.team43.service.PostService;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private PostService postService;

    @GetMapping
    public Page<PostDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @RequestParam(required = false) List<Long> accountIds,
                                @RequestParam(required = false) List<Long> blockedIds,
                                @RequestParam(required = false) String author,
                                @RequestParam(required = false) String text,
                                @RequestParam(required = false, defaultValue = "true") Boolean withFriends,
                                @RequestParam(required = false) Boolean isBlocked,
                                @RequestParam(required = false, defaultValue = "false")  Boolean isDeleted,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo,
                                @RequestParam(required = false) List<String> tags,
                                @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                Pageable pageable) {
        return postService.getAll(ids, accountIds, blockedIds, author, text, withFriends, isBlocked, isDeleted
                                ,dateFrom, dateTo, tags, sort, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PostMapping
    public PostDto addNewPost(@RequestBody PostDto postDto) {
        return postService.addNewPost(postDto);
    }

    @PutMapping
    public ResponseEntity<Long> editPost(@RequestBody PostDto postDto) {
        return postService.editPost(postDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

}
