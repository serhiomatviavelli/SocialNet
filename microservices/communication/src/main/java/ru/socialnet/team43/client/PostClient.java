package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.CommentDto;
import ru.socialnet.team43.dto.PostDto;

import java.time.OffsetDateTime;
import java.util.List;

@FeignClient(name = "databasePostClient", dismiss404 = true, url = "${database.url}" + "/api/v1/post")
public interface PostClient {

    @GetMapping
    Page<PostDto> getAll(@RequestParam List<Long> ids,
                         @RequestParam List<Long> accountIds,
                         @RequestParam List<Long> blockedIds,
                         @RequestParam String author,
                         @RequestParam String text,
                         @RequestParam Boolean withFriends,
                         @RequestParam Boolean isBlocked,
                         @RequestParam Boolean isDeleted,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo,
                         @RequestParam List<String> tags,
                         @RequestParam String sort,
                         Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<PostDto> getPostById(@PathVariable Long id);

    @PostMapping
    PostDto addNewPost(@RequestBody PostDto postDto);

    @PutMapping
    ResponseEntity<Long> editPost(@RequestBody PostDto postDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Long> deletePost(@PathVariable Long id);

    @GetMapping("/{postId}/comment")
    ResponseEntity<Page<CommentDto>> getComments(@PathVariable Long postId,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                 @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                 Pageable pageable);

    @GetMapping("/{postId}/comment/{commentId}/subcomment")
    ResponseEntity<Page<CommentDto>> getSubComments(@PathVariable Long postId,
                                                    @PathVariable Long commentId,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                    @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                    Pageable pageable);

    @PostMapping("/{postId}/comment")
    ResponseEntity<CommentDto> addNewComment(@PathVariable Long postId,
                                             @RequestBody CommentDto commentDto);

    @PutMapping("/{postId}/comment")
    ResponseEntity<CommentDto> editComment(@PathVariable Long postId,
                                           @RequestBody CommentDto commentDto);

    @DeleteMapping("/{postId}/comment/{commentId}")
    ResponseEntity<Long> deleteComment(@PathVariable Long postId,
                                       @PathVariable Long commentId);
}
