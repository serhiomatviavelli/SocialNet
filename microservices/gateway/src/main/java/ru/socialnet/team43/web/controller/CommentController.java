package ru.socialnet.team43.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.CommunicationClient;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.CommentDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.util.ControllerUtil;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/post/{postId}/comment")
public class CommentController {

    private final CommunicationClient communicationClient;
    private final ProfileClient profileClient;
    private final ControllerUtil controllerUtil;

    @GetMapping
    public ResponseEntity<Page<CommentDto>> getComments(@PathVariable Long postId,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                        @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                        Pageable pageable) {
        ResponseEntity<Page<CommentDto>> responseEntity = communicationClient.getComments(postId, isDeleted, sort, pageable);
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

    @GetMapping("/{commentId}/subcomment")
    public ResponseEntity<Page<CommentDto>> getSubComments(@PathVariable Long postId,
                                                           @PathVariable Long commentId,
                                                           @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                                                           @RequestParam(required = false, defaultValue = "time,desc") String sort,
                                                           Pageable pageable) {
        ResponseEntity<Page<CommentDto>> responseEntity = communicationClient.getSubComments(postId, commentId, isDeleted, sort, pageable);
        return controllerUtil.createNewResponseEntity(responseEntity);

    }

    @PostMapping
    public ResponseEntity<CommentDto> addNewComment(@PathVariable Long postId,
                                                    @RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            PersonDto author = profileClient.getMyProfile(userDetails.getUsername()).getBody();
            assert author != null;
            commentDto.setAuthorId(author.getId());
            ResponseEntity<CommentDto> responseEntity = communicationClient.addNewComment(postId, commentDto);
            return controllerUtil.createNewResponseEntity(responseEntity);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public ResponseEntity<CommentDto> editComment(@PathVariable Long postId,
                                                  @RequestBody CommentDto commentDto) {
        ResponseEntity<CommentDto> responseEntity = communicationClient.editComment(postId, commentDto);
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) {
        ResponseEntity<Long> responseEntity = communicationClient.deleteComment(postId, commentId);
        return controllerUtil.createNewResponseEntity(responseEntity);
    }

}
