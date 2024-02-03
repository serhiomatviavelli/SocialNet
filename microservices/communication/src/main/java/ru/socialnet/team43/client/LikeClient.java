package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.LikeDto;

@FeignClient(name = "databaseLikeClient", dismiss404 = true, url = "${database.url}" + "/api/v1")
public interface LikeClient {

    @PostMapping("/post/{id}/like")
    ResponseEntity<LikeDto> likePost(@PathVariable Long id, @RequestBody LikeDto response, @RequestParam String email);

    @DeleteMapping("/post/{id}/like")
    ResponseEntity<Void> deletePostLike(@PathVariable Long id);

    @PostMapping("/post/{id}/comment/{commentId}/like")
    ResponseEntity<LikeDto> likeComment(@PathVariable Long id, @PathVariable Long commentId, @RequestParam String email);

    @DeleteMapping("/post/{id}/comment/{commentId}/like")
    ResponseEntity<Void> deleteCommentLike(@PathVariable Long id, @PathVariable Long commentId);
}
