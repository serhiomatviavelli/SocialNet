package ru.socialnet.team43.service;

import jooq.db.tables.records.LikeRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.LikeDto;
import ru.socialnet.team43.repository.LikeRepository;
import ru.socialnet.team43.repository.PersonIteraction;
import ru.socialnet.team43.repository.PostIteraction;
import ru.socialnet.team43.repository.mapper.LikeToPostMapper;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository repository;
    private final LikeToPostMapper mapper;
    private final PostIteraction postIteraction;
    private final PersonIteraction personIteraction;

    public LikeDto addLikeToPost(Long id, LikeDto likeDto, String email) {
        Long currentUserId = personIteraction.getPersonIdByEmail(email);

        if (postIteraction.getPostById(id).isPresent()) {
            LikeRecord record = repository.addLikeToPost(id, mapper.likeDtoToLikeRecord(likeDto), currentUserId);
            return mapper.likeRecordToLikeDto(record);
        }
        return null;
    }

    public boolean deleteLikePost(Long id) {
        return repository.deleteLikePost(id);
    }

    public LikeDto addLikeToComment(Long commentId, String email) {
        Long currentUserId = personIteraction.getPersonIdByEmail(email);

        if (postIteraction.getPostById(commentId).isPresent()) {
            LikeRecord record = repository.addLikeToComment(commentId, currentUserId);

            return mapper.likeRecordToLikeDto(record);
        }
        return null;
    }

    public boolean deleteCommentLike(Long commentId) {
        return repository.deleteCommentLike(commentId);
    }
}