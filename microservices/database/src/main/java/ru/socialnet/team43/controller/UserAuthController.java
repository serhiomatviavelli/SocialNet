package ru.socialnet.team43.controller;

import jooq.db.tables.records.PersonRecord;
import jooq.db.tables.records.UserAuthRecord;
import jooq.db.tables.records.NotificationSettingRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.RegDtoDb;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.service.RegistrationService;
import ru.socialnet.team43.service.UserAuthService;
import ru.socialnet.team43.service.notifications.NotificationSettingDBService;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class UserAuthController {
    private final RegistrationService regService;
    private final UserAuthService userAuthService;
    private final NotificationSettingDBService settingDBService;

    @PostMapping("/register/create")
    public ResponseEntity<PersonDto> createPerson(@RequestBody RegDtoDb regDtoDb) {
        boolean isSuccessful = false;

        Optional<UserAuthRecord> userAuthRecord = userAuthService.createUserAuth(regDtoDb);

        if (userAuthRecord.isPresent()) {
            long userId = userAuthRecord.get().getId();

            try {
                Optional<PersonRecord> person = regService.createPerson(regDtoDb, userId);
                Optional<NotificationSettingRecord> settingRecord = settingDBService.createSetting(userId);

                isSuccessful = person.isPresent() & settingRecord.isPresent();
            } catch (Exception ex) {
                log.debug(ex.getMessage());
            }

            if(!isSuccessful){
                userAuthService.deleteUserAuthById(userId);
            }
        }

        return isSuccessful ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/register/email")
    public ResponseEntity<Integer> getCountPersonByEmail(@RequestParam("email") String email) {

        int count = userAuthService.getUsersCountByEmail(email);
        return count > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserAuthDto> getUser(@RequestParam("email") String email) {
        Optional<UserAuthDto> user = userAuthService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/change-password")
    public ResponseEntity<UserAuthDto> setNewPassword(@RequestParam("password") String password,
                                                      @RequestParam("email") String email){

        Optional<UserAuthDto> user = userAuthService.setNewPassword(password, email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
