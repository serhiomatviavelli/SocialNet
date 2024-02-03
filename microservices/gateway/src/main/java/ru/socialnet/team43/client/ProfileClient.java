package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.socialnet.team43.dto.CaptchaDto;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.RegDto;
import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;
import ru.socialnet.team43.dto.notifications.NotificationSettingDto;
import ru.socialnet.team43.dto.notifications.NotificationUpdateDto;
import ru.socialnet.team43.dto.notifications.NotificationsDto;
import ru.socialnet.team43.dto.storage.StorageDto;

import java.util.List;

@FeignClient(name = "profileClient", dismiss404 = true, url = "${profile.url}" + "/api/v1")
public interface ProfileClient {

    @PostMapping("/auth/register")
    ResponseEntity<Void> registrationPerson(@RequestBody RegDto regDto);

    @GetMapping("/auth/captcha")
    ResponseEntity<CaptchaDto> getCaptcha();

    @PostMapping("/auth/password/recovery/")
    ResponseEntity<Void> sendEmailRecovery(@RequestParam("email") String email);

    @PostMapping("/auth/password/recovery/{token}")
    ResponseEntity<Void> resetForgotPassword(
            @PathVariable("token") String token, @RequestParam("password") String password);

    @GetMapping("/account/me")
    ResponseEntity<PersonDto> getMyProfile(@RequestParam("email") String email);

    @PutMapping("/account/me")
    ResponseEntity<PersonDto> updateMyProfile(@RequestBody PersonDto dto);

    @DeleteMapping("/account/me")
    ResponseEntity<Void> deleteMyProfile(@RequestParam("email") String email);

    @GetMapping("/account/{id}")
    ResponseEntity<PersonDto> getProfileById(@PathVariable Long id);

    @GetMapping("/geo/country")
    ResponseEntity<List<CountryDto>> getCountry();

    @GetMapping("/geo/country/{countryId}/city")
    ResponseEntity<List<CityDto>> getCitiesByCountryId(@PathVariable Long countryId);

    @PutMapping("/geo/load")
    ResponseEntity<Void> load();

    @PostMapping(path = "/storage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<StorageDto> getStorage(@RequestBody MultipartFile file);

    @GetMapping("/notifications/settings")
    ResponseEntity<NotificationSettingDto> getSettings(@RequestParam("email") String email);

    @PutMapping("/notifications/settings")
    ResponseEntity<NotificationSettingDto> updateSetting(
            @RequestBody NotificationUpdateDto notificationUpdateDto,
            @RequestParam("email") String email);

    @PostMapping("/notifications/settings{id}")
    ResponseEntity<NotificationSettingDto> createSettings(@PathVariable Long id);

    @GetMapping("/notifications")
    ResponseEntity<Page<NotificationsDto>> getAll(
            @RequestParam("email") String email, Pageable pageable);

    @GetMapping("/notifications/count")
    ResponseEntity<CountDto> getNotificationCount(@RequestParam("email") String email);

    @PutMapping("/notifications/readed")
    ResponseEntity<String> setIsRead(@RequestParam("email") String email);

    @GetMapping("/account/search")
    ResponseEntity<Page<PersonDto>> searchAccounts(
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
            Pageable pageable);

    @PutMapping("/account/online")
    ResponseEntity<Void> updateIsOnlineForAccount(@RequestParam String email, @RequestParam boolean isOnline);
}
