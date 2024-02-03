package ru.socialnet.team43.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.util.ControllerUtil;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {
    private final ProfileClient profileClient;
    private final ControllerUtil controllerUtil;

    @GetMapping("/me")
    public ResponseEntity<PersonDto> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("/api/v1/account/me");
        if (userDetails != null) {

            profileClient.updateIsOnlineForAccount(userDetails.getUsername(), true);

            log.info(userDetails.getUsername());
            ResponseEntity<PersonDto> inputResponseEntity =
                    profileClient.getMyProfile(userDetails.getUsername());
            return controllerUtil.createNewResponseEntity(inputResponseEntity);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/me")
    public ResponseEntity<PersonDto> updateMyProfile(
            @RequestBody PersonDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("update person with email: {}", email);
        dto.setEmail(email);
        ResponseEntity<PersonDto> inputResponseEntity = profileClient.updateMyProfile(dto);
        HttpStatusCode statusCode = inputResponseEntity.getStatusCode();
        if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))) {
            return ResponseEntity.badRequest().build();
        }

        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("delete person with email: {}", email);

        HttpStatusCode statusCode = profileClient.deleteMyProfile(email).getStatusCode();

        if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(statusCode).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getProfileById(@PathVariable Long id) {
        log.info("get profile by id: {}", id);
        ResponseEntity<PersonDto> inputResponseEntity = profileClient.getProfileById(id);
        HttpStatusCode statusCode = inputResponseEntity.getStatusCode();

        if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))) {
            return ResponseEntity.badRequest().build();
        }
        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PersonDto>> searchAccounts(
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "false") Boolean isDeleted,
            @RequestParam(defaultValue = "") String statusCode,
            @RequestParam(defaultValue = "0") Integer ageTo,
            @RequestParam(defaultValue = "0") Integer ageFrom,
            @RequestParam(defaultValue = "") String ids,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info(
                "/api/v1/account/search\n"
                        + "author: {}, firstName: {}, lastName: {}, city: {}, country: {}, "
                        + "isDeleted: {}, statusCode: {}, ageTo: {}, ageFrom: {}, ids: {}",
                author,
                firstName,
                lastName,
                city,
                country,
                isDeleted,
                statusCode,
                ageTo,
                ageFrom,
                ids);

        if (userDetails != null) {
            log.info(userDetails.getUsername());

            ResponseEntity<Page<PersonDto>> inputResponseEntity =
                    profileClient.searchAccounts(
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
                            userDetails.getUsername(),
                            pageable);

            return controllerUtil.createNewResponseEntity(inputResponseEntity);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/search/statusCode")
    public ResponseEntity<Page<PersonDto>> searchAccountsByStatus(
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "") String firstName,
            @RequestParam(defaultValue = "") String lastName,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "false") Boolean isDeleted,
            @RequestParam(defaultValue = "") String statusCode,
            @RequestParam(defaultValue = "0") Integer ageTo,
            @RequestParam(defaultValue = "0") Integer ageFrom,
            @RequestParam(defaultValue = "") String ids,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info(
                "/api/v1/account/statusCode\n"
                        + "author: {}, firstName: {}, lastName: {}, city: {}, country: {}, "
                        + "isDeleted: {}, statusCode: {}, ageTo: {}, ageFrom: {}, ids: {}",
                author,
                firstName,
                lastName,
                city,
                country,
                isDeleted,
                statusCode,
                ageTo,
                ageFrom,
                ids);

        if (userDetails != null) {
            log.info(userDetails.getUsername());

            ResponseEntity<Page<PersonDto>> inputResponseEntity =
                    profileClient.searchAccounts(
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
                            userDetails.getUsername(),
                            pageable);

            return controllerUtil.createNewResponseEntity(inputResponseEntity);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
