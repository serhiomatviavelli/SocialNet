package ru.socialnet.team43.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.socialnet.team43.client.CommunicationClient;
import ru.socialnet.team43.dto.LikeDto;
import ru.socialnet.team43.util.ControllerUtil;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private final CommunicationClient communicationClient;
    private final ControllerUtil controllerUtil;

    @PostMapping("post/{id}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable Long id, @RequestBody LikeDto response, @AuthenticationPrincipal UserDetails userDetails) {
        return controllerUtil.createNewResponseEntity(communicationClient.likePost(id, response, userDetails.getUsername()));
    }

    @DeleteMapping("post/{id}/like")
    public ResponseEntity<Void> deletePostLike(@PathVariable Long id) {
        return controllerUtil.createNewResponseEntity(communicationClient.deletePostLike(id));
    }

    @PostMapping("/post/{id}/comment/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable Long id, @PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        return controllerUtil.createNewResponseEntity(communicationClient.likeComment(id, commentId, userDetails.getUsername()));
    }

    @DeleteMapping("post/{id}/comment/{commentId}/like")
    public ResponseEntity<Void> deleteCommentLike(@PathVariable Long id, @PathVariable Long commentId) {
        return controllerUtil.createNewResponseEntity(communicationClient.deleteCommentLike(id, commentId));
    }
}
