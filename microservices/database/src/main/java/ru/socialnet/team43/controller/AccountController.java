package ru.socialnet.team43.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.service.UserAuthService;
import ru.socialnet.team43.util.ControllerUtil;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private final UserAuthService userAuthService;
    private final ControllerUtil controllerUtil;

    @GetMapping("/me")
    public ResponseEntity<PersonDto> getAccountInfo(@RequestParam("email") String email) {
        Optional<PersonDto> accountInfo = userAuthService.getAccountInfo(email);
        return accountInfo
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getAccountById(@PathVariable Long id) {
        Optional<PersonDto> accountInfo = userAuthService.getAccountById(id);
        return accountInfo
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<PersonDto> updateAccount(@RequestBody PersonDto dto) {
        Optional<PersonDto> accountInfo = userAuthService.updateAccount(dto);
        return accountInfo
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@RequestParam("email") String email) {
        int result = userAuthService.deleteAccount(email);
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/accountsSearch")
    public ResponseEntity<Page<PersonDto>> getAccountsSearchResult(
            @RequestParam("userName") String userName,
            @RequestParam(value = "searchDto") String searchDtoStr,
            Pageable pageable) {
        try {
            AccountSearchDto accountSearchDto =
                    controllerUtil.stringToObject(searchDtoStr, AccountSearchDto.class);
            Page<PersonDto> personDtoPage =
                    userAuthService.getAccountsSearchResult(userName, accountSearchDto, pageable);
            return ResponseEntity.ok(personDtoPage);
        } catch (Exception ex) {
            log.error(AccountController.class.getCanonicalName(), ex);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/online")
    public boolean updateIsOnlineForAccount(
            @RequestParam String email, @RequestParam boolean isOnline) {
        return userAuthService.updateIsOnline(email, isOnline);
    }
}
