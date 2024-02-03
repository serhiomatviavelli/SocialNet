package ru.socialnet.team43.web.controller;

import feign.FeignException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.*;
import ru.socialnet.team43.security.SecurityService;
import ru.socialnet.team43.service.UserService;
import ru.socialnet.team43.util.ControllerUtil;
import ru.socialnet.team43.web.model.AuthRequest;
import ru.socialnet.team43.web.model.JwtResponse;
import ru.socialnet.team43.web.model.RefreshRequest;
import ru.socialnet.team43.web.model.SimpleResponse;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final SecurityService securityService;
    private final ProfileClient profileClient;
    private final ControllerUtil controllerUtil;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private AtomicInteger usersCount;

    public AuthController(SecurityService securityService, ProfileClient profileClient, ControllerUtil controllerUtil, PasswordEncoder passwordEncoder, UserService userService, MeterRegistry meterRegistry) {
        this.securityService = securityService;
        this.profileClient = profileClient;
        this.controllerUtil = controllerUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        meterRegistry.gauge("users_count", usersCount);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> signIn(@RequestBody AuthRequest authRequest) {
        log.debug("/api/v1/auth/login");
        log.debug(authRequest.getEmail());

        JwtResponse responseBody = securityService.authenticateUser(authRequest);
        profileClient.updateIsOnlineForAccount(authRequest.getEmail(), true);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        log.debug("/api/v1/auth/refresh");
        log.debug(refreshRequest.toString());

        try {
            JwtResponse responseBody = securityService.refreshToken(refreshRequest);
            return ResponseEntity.ok(responseBody);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        log.debug("/api/v1/auth/logout");
        log.debug(userDetails.getUsername());

        SimpleResponse responseBody = securityService.logout(userDetails.getUsername());
        profileClient.updateIsOnlineForAccount(userDetails.getUsername(), false);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registrationPerson(@RequestBody RegDto regDto) {
        log.debug("/api/v1/auth/register");
        log.debug(regDto.toString());

        if (securityService.doPasswordsMatch(regDto)) {
            ResponseEntity<Void> inputResponseEntity =
                    profileClient.registrationPerson(
                            securityService.getRegDtoWithEncryptedPassword(regDto));
            HttpStatusCode statusCode = inputResponseEntity.getStatusCode();

            if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))) {
                return ResponseEntity.badRequest().build();
            }

            return controllerUtil.createNewResponseEntity(inputResponseEntity);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDto> getCaptcha() {
        log.debug("/api/v1/auth/captcha");

        ResponseEntity<CaptchaDto> inputResponseEntity = profileClient.getCaptcha();
        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @PostMapping("/password/recovery/")
    public ResponseEntity<Void> sendEmail(@RequestBody PasswordRecoveryDto dto) {
        String email = dto.getEmail();
        log.info("/password/recovery/ for email: {}", email);

        if ((email == null) || userService.findUserByEmail(email).isEmpty()) {
            log.info("user not found with email: {}", email);
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<Void> inputResponseEntity = profileClient.sendEmailRecovery(email);
        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @PostMapping("/password/recovery/{token}")
    public ResponseEntity<Void> resetForgotPassword(@PathVariable("token") String token,
                                                    @RequestBody NewPasswordDto dto) {

        log.info("Token {}, dto: {}", token, dto);

        if (token == null || dto == null || dto.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        String encodePassword = passwordEncoder.encode(dto.getPassword());
        ResponseEntity<Void> inputResponseEntity = profileClient.resetForgotPassword(token, encodePassword);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<Void> handler(FeignException ex) {
        log.warn("Error in the gateway {}", ex.getMessage());
        return ResponseEntity.status(ex.status()).build();
    }


}
