package ru.socialnet.team43.sevice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.socialnet.team43.dto.friends.FriendCountDto;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.friends.FriendRecommendDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.dto.PersonDto;

import java.util.List;

public interface FriendService {

    Page<FriendSearchResponseDto> searchFriendsByStatus(String statusCode, String email, Pageable page);

    List<PersonDto> searchFriends(String statusCode,
                                  String  firstName,
                                  String  ageFrom,
                                  String  ageTo,
                                  String  country,
                                  String  city,
                                  String  email,
                                  Pageable page);

    ResponseEntity<FriendDto> approveFriendRequest(Long id, String email);

    ResponseEntity<FriendDto> friendRequest(Long id, String email);

    ResponseEntity<FriendDto> getFriendsById(Long id, String email);

    ResponseEntity<FriendDto> subscribe(Long id, String email);

    ResponseEntity<FriendDto> block(Long id, String email);

    ResponseEntity<FriendDto> unblock(Long id, String email);

    ResponseEntity<FriendCountDto> getFriendsCount(String email);

    ResponseEntity<List<FriendRecommendDto>> getRecommendations(String email);
}
