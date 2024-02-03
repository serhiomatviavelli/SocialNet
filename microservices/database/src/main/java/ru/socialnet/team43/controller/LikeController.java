package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.socialnet.team43.dto.LikeDto;
import ru.socialnet.team43.service.LikeService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private LikeService likeService;

    @PostMapping("/post/{id}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable Long id, @RequestBody LikeDto likeDto, String email) {
        return ResponseEntity.ok().body(likeService.addLikeToPost(id, likeDto, email));
    }

    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<Void> deleteLikePost(@PathVariable Long id) {
        return likeService.deleteLikePost(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build() ;
    }

    @PostMapping("/post/{id}/comment/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable Long id, @PathVariable Long commentId, String email) {
        return ResponseEntity.ok().body(likeService.addLikeToComment(commentId, email));
    }

    @DeleteMapping("/post/{id}/comment/{commentId}/like")
    public ResponseEntity<Void> deleteLikeComment(@PathVariable Long id, @PathVariable Long commentId) {
        return likeService.deleteCommentLike(commentId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build() ;
    }
}
