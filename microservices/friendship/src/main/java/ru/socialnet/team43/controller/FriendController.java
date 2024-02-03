package ru.socialnet.team43.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.friends.FriendCountDto;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.friends.FriendRecommendDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.sevice.FriendService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final DatabaseClient client;
    private final FriendService friendService;

    @GetMapping("/count")
    public ResponseEntity<FriendCountDto> getFriendCount(@RequestParam String email) {
        return friendService.getFriendsCount(email);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendRecommendDto>> getRecommendations(@RequestParam String email) {
        return friendService.getRecommendations(email);
    }

    @GetMapping("")
    public ResponseEntity<List<PersonDto>> searchFriends(@RequestParam String statusCode,
                                                         @RequestParam(defaultValue = "") String firstName,
                                                         @RequestParam(defaultValue = "0") String ageFrom,
                                                         @RequestParam(defaultValue = "99") String ageTo,
                                                         @RequestParam(defaultValue = "") String country,
                                                         @RequestParam(defaultValue = "") String city,
                                                         @RequestParam String email,
                                                         Pageable page) {

        return ResponseEntity.ok(friendService.searchFriends(statusCode,
                firstName,
                ageFrom,
                ageTo,
                country,
                city,
                email,
                page));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<FriendSearchResponseDto>> searchFriendsByStatus(@RequestParam String statusCode,
                                                                               @RequestParam String email,
                                                                               Pageable page) {

        return ResponseEntity.ok(friendService.searchFriendsByStatus(statusCode, email, page));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<FriendDto> approveFriendRequest(@PathVariable Long id, @RequestParam String email) {

        return friendService.approveFriendRequest(id, email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @RequestParam String email) {

        return client.deleteFriend(id, email);
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<FriendDto> friendRequest(@PathVariable Long id, @RequestParam String email) {

        return friendService.friendRequest(id, email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriendById(@PathVariable Long id, @RequestParam String email) {

        return friendService.getFriendsById(id, email);
    }

    @PostMapping("/subscribe/{id}")
    public ResponseEntity<FriendDto> subscribe(@PathVariable Long id, @RequestParam String email) {

        return friendService.subscribe(id, email);
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<FriendDto> block(@PathVariable Long id, @RequestParam String email) {

        return friendService.block(id, email);
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendDto> unblock(@PathVariable Long id, @RequestParam String email) {

        return friendService.unblock(id, email);
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<Void> handler(FeignException ex) {
        log.warn("Error in the gateway {}", ex.getMessage());
        return ResponseEntity.status(ex.status()).build();
    }

}
