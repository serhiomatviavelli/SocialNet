package ru.socialnet.team43.service;

import lombok.RequiredArgsConstructor;
import org.jooq.impl.QOM;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.enums.FriendshipStatus;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.dto.enums.NotificationType;
import ru.socialnet.team43.repository.FriendRepository;
import ru.socialnet.team43.repository.PersonRepository;
import ru.socialnet.team43.service.notifications.NotificationDBService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepo;
    private final PersonRepository personRepo;
    private final NotificationDBService notificationDBService;

    public int getFriendsCount(String email) {
        return friendRepo.getFriendsCount(email);
    }

    public Map<Long, Integer> getRecommendations(String email) {
        return friendRepo.getRecommendations(email);
    }

    public List<PersonDto> searchFriends(String status,
                                         String firstName,
                                         Integer ageFrom,
                                         Integer ageTo,
                                         String country,
                                         String city,
                                         String email,
                                         Pageable page) {

        return friendRepo.searchFriends(status, firstName, ageFrom, ageTo, country, city, email, page);
    }

    public List<FriendSearchResponseDto> searchFriendsByStatus(String statusCode, String email, Pageable page) {

        return friendRepo.findFriendsIdsByStatus(statusCode, email, page);
    }

    public void approveFriendRequest(Long id, String email) {
        Long accountId = personRepo.getPersonIdByEmail(email);
        String srcFriendStatus = friendRepo.getStatus(accountId, id);
        String dscFriendStatus = friendRepo.getStatus(id, accountId);

        if (!dscFriendStatus.equals("BLOCKED")){
            if (srcFriendStatus.equals("REQUEST_FROM")) {
                friendRepo.updateStatus(accountId, id, "FRIEND");
            }
        }
    }

    public void deleteFriend(Long id, String email) {
        Long accountId = personRepo.getPersonIdByEmail(email);
        friendRepo.deleteFriendship(id, accountId);
    }

    public void friendRequest(Long id, String email) {
        Long accountId = personRepo.getPersonIdByEmail(email);
        String srcFriendStatus = friendRepo.getStatus(accountId, id);
        String dscFriendStatus = friendRepo.getStatus(id, accountId);

        if (srcFriendStatus.equals("REQUEST_FROM")) {
            friendRepo.updateStatus(accountId, id, "FRIEND");
        } else if ((!srcFriendStatus.equals("FRIEND"))
                && (!dscFriendStatus.equals("BLOCKED"))) {
            friendRepo.deleteFriendship(id, accountId);
            friendRepo.saveFriendship(accountId, id, "REQUEST_TO", "REQUEST_FROM");

            notificationDBService.addNewFriendEvent(id, accountId, NotificationType.FRIEND_REQUEST.getId());
        }
    }

    public ResponseEntity<FriendDto> getFriendsById(Long id, String email) {

        Optional<FriendDto> optionalFriendDto = friendRepo.getOptionalFriendDtoById(id, email);

        return optionalFriendDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    public void subscribe(Long id, String email) {
        Long accountId = personRepo.getPersonIdByEmail(email);
        String friendStatus = friendRepo.getStatus(id, accountId);

        if(!friendStatus.equals("BLOCKED")){
            friendRepo.deleteFriendship(id, accountId);
            friendRepo.saveFriendship(accountId, id, "WATCHING", "SUBSCRIBED");
        }

    }

    public void block(Long id, String email) {
        Long accountId = personRepo.getPersonIdByEmail(email);
        friendRepo.deleteFriendship(id, accountId);
        friendRepo.save(accountId, id, "BLOCKED");

    }

    public void unblock(Long id, String email) {
        Long accountId = personRepo.getPersonIdByEmail(email);
        friendRepo.delete(accountId, id);
    }

    public int getCountSearchByStatus(String statusCode, String email) {
        return friendRepo.getCountSearchByStatus(statusCode, email);
    }
}
