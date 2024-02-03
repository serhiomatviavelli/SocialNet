package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.service.ProfileService;
import ru.socialnet.team43.util.ControllerUtil;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final DatabaseClient databaseClient;
    private final ControllerUtil controllerUtil;
    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<PersonDto> getMyProfile(@RequestParam("email") String email) {
        ResponseEntity<PersonDto> inputResponseEntity = databaseClient.getAccountInfo(email);
        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getProfileById(@PathVariable Long id) {
        ResponseEntity<PersonDto> inputResponseEntity = databaseClient.getProfileById(id);
        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @PutMapping("/me")
    public ResponseEntity<PersonDto> updateMyProfile(@RequestBody PersonDto dto) {
        ResponseEntity<PersonDto> inputResponseEntity = databaseClient.updateMyProfile(dto);
        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@RequestParam("email") String email) {
        return databaseClient.deleteMyProfile(email);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PersonDto>> searchAccounts(
            @RequestParam String author,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String city,
            @RequestParam String country,
            @RequestParam Boolean isDeleted,
            @RequestParam String statusCode,
            @RequestParam Integer ageTo,
            @RequestParam Integer ageFrom,
            @RequestParam String ids,
            @RequestParam String userName,
            Pageable pageable) {

        try {
            ResponseEntity<Page<PersonDto>> inputResponseEntity =
                    profileService.proceedSearch(
                            author,
                            firstName,
                            lastName,
                            city,
                            country,
                            isDeleted,
                            statusCode,
                            ageTo,
                            ageFrom,
                            ids,
                            userName,
                            pageable);
            return controllerUtil.createNewResponseEntity(inputResponseEntity);
        } catch (Exception ex) {
            log.error(AccountController.class.getCanonicalName(), ex);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/online")
    public ResponseEntity<Void> updateIsOnlineForAccount(String email, boolean isOnline) {
        boolean result = databaseClient.updateIsOnlineForAccount(email, isOnline);
        return result ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
