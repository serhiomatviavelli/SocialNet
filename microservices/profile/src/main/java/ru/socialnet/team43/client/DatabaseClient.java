package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.RegDtoDb;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.dto.storage.StorageDto;

@FeignClient(name = "databaseClient", dismiss404 = true, url = "${database.url}")
public interface DatabaseClient {

    @GetMapping("/auth/user")
    UserAuthDto getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/auth/register/email")
    ResponseEntity<Void> isEmailExist(@RequestParam("email") String email);

    @PostMapping("/auth/register/create")
    ResponseEntity<Void> createPerson(RegDtoDb regDtoDb);

    @GetMapping("/account/me")
    ResponseEntity<PersonDto> getAccountInfo(@RequestParam("email") String email);

    @DeleteMapping("/account/me")
    ResponseEntity<Void> deleteMyProfile(@RequestParam("email") String email);

    @PutMapping("/account/me")
    ResponseEntity<PersonDto> updateMyProfile(@RequestBody PersonDto dto);

    @GetMapping("/account/{id}")
    ResponseEntity<PersonDto> getProfileById(@PathVariable Long id);

    @PostMapping(path = "/storage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<StorageDto> getStorage(@RequestBody MultipartFile file);

    @GetMapping("/account/accountsSearch")
    ResponseEntity<Page<PersonDto>> getAccountsSearchResult(
            @RequestParam("userName")String userName,
            @RequestParam("searchDto") String searchDtoStr,
            Pageable pageableStr);

    @PutMapping("/account/online")
    boolean updateIsOnlineForAccount(@RequestParam String email, @RequestParam boolean isOnline);

    @PutMapping("/auth/change-password")
    ResponseEntity<UserAuthDto> setNewPassword(
            @RequestParam("password") String password, @RequestParam("email") String email);
}
