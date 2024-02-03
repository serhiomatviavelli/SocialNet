package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.friends.FriendCountDto;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.friends.FriendRecommendDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.dto.PersonDto;

import java.util.List;

@FeignClient(name = "friendClient", url = "${environment.url}" + "/api/v1/friends")
public interface FriendClient {

    @GetMapping("/count")
    ResponseEntity<FriendCountDto> getFriendsCount(@RequestParam String email);

    @GetMapping("/recommendations")
    ResponseEntity<List<FriendRecommendDto>> getRecommendations(@RequestParam String email);

    @GetMapping("")
    ResponseEntity<List<PersonDto>> searchFriends(@RequestParam String statusCode,
                                                  @RequestParam(defaultValue = "") String firstName,
                                                  @RequestParam(defaultValue = "0") String ageFrom,
                                                  @RequestParam(defaultValue = "99") String ageTo,
                                                  @RequestParam(defaultValue = "") String country,
                                                  @RequestParam(defaultValue = "") String city,
                                                  @RequestParam String email,
                                                  Pageable page);
    @GetMapping("/status")
    ResponseEntity<Page<FriendSearchResponseDto>> searchFriendsByStatus(@RequestParam String statusCode,
                                                                        @RequestParam String email,
                                                                        Pageable page);

    @PutMapping("/{id}/approve")
    ResponseEntity<FriendDto> approveFriendRequest(@PathVariable Long id, @RequestParam String email);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteFriend(@PathVariable Long id, @RequestParam String email);
    @PostMapping("/{id}/request")
    ResponseEntity<FriendDto> friendRequest(@PathVariable Long id, @RequestParam String email);

    @GetMapping("/{id}")
    ResponseEntity<FriendDto> getFriendsById(@PathVariable Long id, @RequestParam String email);
    @PostMapping("/subscribe/{id}")
    ResponseEntity<FriendDto> subscribe(@PathVariable Long id, @RequestParam String email);
    @PutMapping("/block/{id}")
    ResponseEntity<FriendDto> block(@PathVariable Long id, @RequestParam String email);
    @PutMapping("/unblock/{id}")
    ResponseEntity<FriendDto> unblock(@PathVariable  Long id, @RequestParam String email);
}
