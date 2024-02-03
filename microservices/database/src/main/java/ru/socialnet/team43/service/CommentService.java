package ru.socialnet.team43.service;

import jooq.db.tables.records.CommentRecord;
import jooq.db.tables.records.PostRecord;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.CommentDto;
import ru.socialnet.team43.dto.enums.NotificationType;
import ru.socialnet.team43.repository.CommentRepository;
import ru.socialnet.team43.repository.PostRepository;
import ru.socialnet.team43.repository.mapper.CommentDtoCommentRecordMapper;
import ru.socialnet.team43.service.notifications.NotificationDBService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentDtoCommentRecordMapper mapper;
    private NotificationDBService notificationDBService;

    public ResponseEntity<Page<CommentDto>> getComments(Long postId, Boolean isDeleted, String sort, Pageable pageable) {
        Page<CommentDto> comments = commentRepository.getComments(postId, isDeleted, sort, pageable);
        return ResponseEntity.ok().body(comments);
    }

    public ResponseEntity<Page<CommentDto>> getSubComments(Long postId, Long commentId, Boolean isDeleted,
                                                           String sort, Pageable pageable) {
        Page<CommentDto> subComments = commentRepository.getSubComments(postId, commentId, isDeleted, sort, pageable);
        return ResponseEntity.ok().body(subComments);
    }

    public ResponseEntity<CommentDto> addNewComment(Long postId, CommentDto commentDto) {
        CommentRecord commentRecord = mapper.commentDtoToCommentRecord(commentDto);
        Long commentId = commentRepository.addNewComment(postId, commentRecord);
        Optional<CommentRecord> comment = commentRepository.getCommentById(commentId);
        if (Objects.equals(commentRecord.getCommentType(), "COMMENT")) {
            comment.ifPresent(postCommentRecord -> commentRepository.incrementCommentsCount(postCommentRecord));

            notificationDBService.addNewEvent(commentId, NotificationType.COMMENT_COMMENT.getId());
        } else if (Objects.equals(commentRecord.getCommentType(), "POST") && comment.isPresent()){
            Optional<PostRecord> post = postRepository.getPostById(comment.get().getPostId());
            post.ifPresent(postRecord -> postRepository.incrementCommentsCount(postRecord));

            notificationDBService.addNewEvent(commentId, NotificationType.POST_COMMENT.getId());
        }

        return comment.isEmpty() ? null :
                ResponseEntity.ok().body(mapper.commentRecordToCommentDto(comment.get()));
    }

    public ResponseEntity<CommentDto> editComment(CommentDto commentDto) {
        if (commentRepository.getCommentById(commentDto.getId()).isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            CommentRecord commentRecord = mapper.commentDtoToCommentRecordForEdit(commentDto);
            commentRepository.editComment(commentRecord);
            return ResponseEntity.ok().body(mapper.commentRecordToCommentDto(commentRecord));
        }
    }

    public ResponseEntity<Long> deleteComment(Long postId, Long commentId) {
        Optional<PostRecord> post = postRepository.getPostById(postId);
        Optional<CommentRecord> comment = commentRepository.getCommentById(commentId);
        if (post.isPresent() && comment.isPresent() && Objects.equals(comment.get().getCommentType(), "POST")) {
            deleteCommentToPost(postId, commentId, post.get(), comment.get());
        } else if(comment.isPresent() && Objects.equals(comment.get().getCommentType(), "COMMENT")) {
            deleteCommentToComment(postId, commentId, comment.get());
        }
        return ResponseEntity.ok().body(commentId);
    }

    public void deleteCommentToPost(Long postId,
                                    Long commentId,
                                    PostRecord post,
                                    CommentRecord comment) {
        List<CommentRecord> subComments = commentRepository.getSubCommentsByParentId(commentId);
        subComments.forEach(subComment -> {
            if (!subComment.getIsDeleted()) {
                commentRepository.deleteComment(postId, subComment.getId());
                commentRepository.decrementCommentsCount(comment);
            }
        });
        postRepository.decrementCommentsCount(post);
        commentRepository.deleteComment(postId, commentId);
    }

    public void deleteCommentToComment(Long postId, Long commentId, CommentRecord comment) {
        Optional<CommentRecord> parentComment = commentRepository.getCommentById(comment.getParentId());
        if (parentComment.isPresent()) {
            commentRepository.decrementCommentsCount(parentComment.get());
            commentRepository.deleteComment(postId, commentId);
        }
    }

}
