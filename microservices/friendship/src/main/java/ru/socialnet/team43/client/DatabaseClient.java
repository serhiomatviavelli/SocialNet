package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "databaseClient", dismiss404 = true, url = "${database.url}" + "/friends")
public interface DatabaseClient {

    @GetMapping("/count")
    int getFriendsCount(@RequestParam String email);

    @GetMapping("/recommendations")
    Map<Long, Integer> getRecommendations(@RequestParam String email);

    @GetMapping("")
    List<PersonDto> searchFriends(@RequestParam String statusCode,
                                  @RequestParam(defaultValue = "") String firstName,
                                  @RequestParam(defaultValue = "0") String ageFrom,
                                  @RequestParam(defaultValue = "99") String ageTo,
                                  @RequestParam(defaultValue = "") String country,
                                  @RequestParam(defaultValue = "") String city,
                                  @RequestParam String email,
                                  Pageable page);

    @GetMapping("/status")
    List<FriendSearchResponseDto>  searchFriendsByStatus(@RequestParam String statusCode,
                                                         @RequestParam String email,
                                                         Pageable page);

    @PutMapping("/{id}/approve")
    ResponseEntity<Void> approveFriendRequest(@PathVariable Long id, @RequestParam String email);
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteFriend(@PathVariable Long id, @RequestParam String email);
    @PostMapping("/{id}/request")
    ResponseEntity<Void> friendRequest(@PathVariable Long id, @RequestParam String email);
    @GetMapping("/{id}")
    ResponseEntity<FriendDto> getFriendsById(@PathVariable Long id, @RequestParam String email);
    @PostMapping("/subscribe/{id}")
    ResponseEntity<Void> subscribe(@PathVariable Long id, @RequestParam String email);
    @PutMapping("/block/{id}")
    ResponseEntity<Void> block(@PathVariable Long id, @RequestParam String email);
    @PutMapping("/unblock/{id}")
    ResponseEntity<Void> unblock(@PathVariable Long id, @RequestParam String email);
    @GetMapping("/status/count")
    int getCountSearchByStatus(@RequestParam String statusCode, @RequestParam String email);
}
