package ru.socialnet.team43.repository;

import jooq.db.tables.records.PostRecord;
import org.jooq.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.socialnet.team43.dto.PostDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PostIteraction {

    Page<PostDto> getAll(List<Long> ids, List<Long> accountsId,
                         List<Long> blockedIds, String author,
                         String text, Boolean withFriends,
                         Boolean isBlocked, Boolean isDeleted,
                         OffsetDateTime dateFrom, OffsetDateTime dateTo,
                         List<String> tags, String sort, Pageable pageable);

    Condition getAllPostCondition(List<Long> ids, List<Long> accountIds,
                                  List<Long> blockedIds, String author,
                                  String text, Boolean withFriends,
                                  Boolean isBlocked, Boolean isDeleted,
                                  OffsetDateTime dateFrom, OffsetDateTime dateTo,
                                  List<String> tags);

    List<Long> authorsIdsByFullName(String author);

    Object sorted(String sort);

    Optional<PostRecord> getPostById(Long id);

    Long addNewPost(PostRecord postRecord);

    void publishQueuedPost(Long id);

    void editPost(PostRecord postRecord);

    void deletePostById(Long id);
}
