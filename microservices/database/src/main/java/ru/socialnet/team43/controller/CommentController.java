package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.CommentDto;
import ru.socialnet.team43.service.CommentService;

@AllArgsConstructor
@Controller
@RequestMapping("/api/v1/post")
public class CommentController {

    private CommentService commentService;

    @GetMapping("/{postId}/comment")
    public ResponseEntity<Page<CommentDto>> getComments(@PathVariable Long postId,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                        @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                        Pageable pageable) {
        return commentService.getComments(postId, isDeleted, sort, pageable);
    }

    @GetMapping("/{postId}/comment/{commentId}/subcomment")
    public ResponseEntity<Page<CommentDto>> getSubComments(@PathVariable Long postId,
                                                           @PathVariable Long commentId,
                                                           @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                           @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                           Pageable pageable) {
        return commentService.getSubComments(postId, commentId, isDeleted, sort, pageable);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDto> addNewComment(@PathVariable Long postId,
                                                    @RequestBody CommentDto commentDto) {
        return commentService.addNewComment(postId, commentDto);
    }

    @PutMapping("/{postId}/comment")
    public ResponseEntity<CommentDto> editComment(@PathVariable Long postId,
                                                  @RequestBody CommentDto commentDto) {
        commentDto.setPostId(postId);
        return commentService.editComment(commentDto);
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) {
        return commentService.deleteComment(postId, commentId);
    }

}
