package ru.socialnet.team43.sevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.configuration.RatingProperties;
import ru.socialnet.team43.dto.friends.FriendCountDto;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.friends.FriendRecommendDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.enums.FriendshipStatus;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final DatabaseClient client;
    private final RatingProperties ratingProperties;

    @Override
    public Page<FriendSearchResponseDto> searchFriendsByStatus(String statusCode, String email, Pageable page) {
        int count = client.getCountSearchByStatus(statusCode, email);
        List<FriendSearchResponseDto> friendIds = client.searchFriendsByStatus(statusCode, email, page);

        return new PageImpl<>(friendIds, page, count);
    }

    @Override
    public List<PersonDto> searchFriends(String statusCode, String firstName, String ageFrom, String ageTo, String country, String city, String email, Pageable page) {
        return client.searchFriends(statusCode,
                firstName,
                ageFrom,
                ageTo,
                country,
                city,
                email,
                page);
    }

    @Override
    public ResponseEntity<FriendDto> approveFriendRequest(Long id, String email) {

        client.approveFriendRequest(id, email);

        return ResponseEntity.ok(new FriendDto(FriendshipStatus.FRIEND, id));
    }

    @Override
    public ResponseEntity<FriendDto> friendRequest(Long id, String email) {

        client.friendRequest(id, email);

        return ResponseEntity.ok(new FriendDto(FriendshipStatus.REQUEST_TO, id));
    }

    @Override
    public ResponseEntity<FriendDto> getFriendsById(Long id, String email) {
        return client.getFriendsById(id, email);
    }

    @Override
    public ResponseEntity<FriendDto> subscribe(Long id, String email) {

        client.subscribe(id, email);

        return ResponseEntity.ok(new FriendDto(FriendshipStatus.WATCHING, id));

    }

    @Override
    public ResponseEntity<FriendDto> block(Long id, String email) {

        client.block(id, email);

        return ResponseEntity.ok(new FriendDto(FriendshipStatus.BLOCKED, id));
    }

    @Override
    public ResponseEntity<FriendDto> unblock(Long id, String email) {

        client.unblock(id, email);

        return ResponseEntity.ok(new FriendDto(FriendshipStatus.NONE, id));
    }

    @Override
    public ResponseEntity<FriendCountDto> getFriendsCount(String email) {
        int countFriends = client.getFriendsCount(email);
        return ResponseEntity.ok(new FriendCountDto(countFriends));
    }

    @Override
    public ResponseEntity<List<FriendRecommendDto>> getRecommendations(String email) {
        Map<Long, Integer> resultMap = client.getRecommendations(email);

        if (resultMap.isEmpty()){
            return ResponseEntity.ok(Collections.emptyList());
        }

        addRatingToMap(resultMap);
        Set<Map.Entry<Long, Integer>> resultSet = resultMap.entrySet();

        return ResponseEntity.ok(resultSet.stream()
                .map(s -> new FriendRecommendDto(
                        s.getKey(),
                        s.getValue()))
                .collect(Collectors.toList()));
    }

    private void addRatingToMap(Map<Long, Integer> map) {
        Set<Map.Entry<Long, Integer>> resultSet = map.entrySet();

        int maxCount = Collections.max(map.values());
        int maxRating = ratingProperties.getMax();
        int minRating = ratingProperties.getMin();
        for (Map.Entry<Long, Integer> entry : resultSet) {
            int value = entry.getValue();
            int rating = ((value * (maxRating - minRating)) / maxCount) + minRating;
            entry.setValue(rating);
        }
    }
}
