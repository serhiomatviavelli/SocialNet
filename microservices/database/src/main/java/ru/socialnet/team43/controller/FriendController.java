package ru.socialnet.team43.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.service.FriendService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService service;

    @GetMapping("/count")
    public int getFriendCount(@RequestParam String email) {
        return service.getFriendsCount(email);
    }

    @GetMapping("/recommendations")
    public Map<Long, Integer> getRecommendations(@RequestParam String email) {
        return service.getRecommendations(email);
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
        List<PersonDto> result = service.searchFriends(statusCode,
                firstName,
                Integer.parseInt(ageFrom),
                Integer.parseInt(ageTo),
                country,
                city,
                email,
                page);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<List<FriendSearchResponseDto>> searchFriendsByStatus(@RequestParam String statusCode,
                                                                               @RequestParam String email,
                                                                               Pageable page) {
        List<FriendSearchResponseDto> result = service.searchFriendsByStatus(statusCode, email, page);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveFriendRequest(@PathVariable Long id, @RequestParam String email) {
        service.approveFriendRequest(id, email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @RequestParam String email) {
        service.deleteFriend(id, email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<Void> friendRequest(@PathVariable Long id, @RequestParam String email) {
        service.friendRequest(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriendById(@PathVariable Long id, @RequestParam String email) {
        return service.getFriendsById(id, email);
    }

    @PostMapping("/subscribe/{id}")
    public ResponseEntity<Void> subscribe(@PathVariable Long id, @RequestParam String email) {
        service.subscribe(id, email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<Void> block(@PathVariable Long id, @RequestParam String email) {
        service.block(id, email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<Void> unblock(@PathVariable Long id, @RequestParam String email) {
        service.unblock(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/count")
    public int getCountSearchByStatus(@RequestParam String statusCode, @RequestParam String email) {
        return service.getCountSearchByStatus(statusCode, email);
    }

}
