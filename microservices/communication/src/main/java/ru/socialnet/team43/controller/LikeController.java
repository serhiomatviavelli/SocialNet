package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.socialnet.team43.client.LikeClient;
import ru.socialnet.team43.dto.LikeDto;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private LikeClient likeClient;

    @PostMapping("/post/{id}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable Long id, @RequestBody LikeDto response, String email) {
        return likeClient.likePost(id, response, email);
    }

    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<Void> deletePostLike(@PathVariable Long id) {
        return likeClient.deletePostLike(id);
    }

    @PostMapping("/post/{id}/comment/{commentId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable Long id, @PathVariable Long commentId, @RequestParam String email) {
        return likeClient.likeComment(id, commentId, email);
    }

    @DeleteMapping("/post/{id}/comment/{commentId}/like")
    public ResponseEntity<Void> deletePostLike(@PathVariable Long id, @PathVariable Long commentId) {
        return likeClient.deleteCommentLike(id, commentId);
    }
}
