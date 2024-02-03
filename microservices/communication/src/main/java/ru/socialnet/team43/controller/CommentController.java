package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.PostClient;
import ru.socialnet.team43.dto.CommentDto;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/post/{postId}/comment")
public class CommentController {

    private PostClient postClient;

    @GetMapping
    public ResponseEntity<Page<CommentDto>> getComments(@PathVariable Long postId,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                        @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                        Pageable pageable) {
        return postClient.getComments(postId, isDeleted, sort, pageable);
    }

    @GetMapping("/{commentId}/subcomment")
    public ResponseEntity<Page<CommentDto>> getSubComments(@PathVariable Long postId,
                                                           @PathVariable Long commentId,
                                                           @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                           @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                           Pageable pageable) {
        return postClient.getSubComments(postId, commentId, isDeleted, sort, pageable);
    }

    @PostMapping
    public ResponseEntity<CommentDto> addNewComment(@PathVariable Long postId,
                                                    @RequestBody CommentDto commentDto) {
        return postClient.addNewComment(postId, commentDto);
    }

    @PutMapping
    public ResponseEntity<CommentDto> editComment(@PathVariable Long postId,
                                                  @RequestBody CommentDto commentDto) {
        return postClient.editComment(postId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) {
        return postClient.deleteComment(postId, commentId);
    }

}
