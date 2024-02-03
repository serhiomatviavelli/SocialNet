package ru.socialnet.team43.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.socialnet.team43.dto.RegDto;
import ru.socialnet.team43.exception.RefreshTokenException;
import ru.socialnet.team43.security.jwt.JwtUtils;
import ru.socialnet.team43.web.model.AuthRequest;
import ru.socialnet.team43.web.model.JwtResponse;
import ru.socialnet.team43.web.model.RefreshRequest;
import ru.socialnet.team43.web.model.SimpleResponse;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> refreshTokenCache = new HashMap<>();

    @Override
    public JwtResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getEmail(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        return generateJwtResponse(userDetails);
    }

    @Override
    public JwtResponse refreshToken(RefreshRequest refreshRequest) throws RefreshTokenException {
        String refreshToken = refreshRequest.getRefreshToken();

        try {
            if (StringUtils.hasText(refreshToken) && jwtUtils.isRefreshTokenValid(refreshToken)) {
                String userName = jwtUtils.getUserNameFromRefreshToken(refreshToken);

                String savedRefreshToken = refreshTokenCache.get(userName);

                if (StringUtils.hasText(savedRefreshToken)
                        && savedRefreshToken.equals(refreshToken)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                    return generateJwtResponse(userDetails);
                } else {
                    throw new RefreshTokenException(refreshToken, "Refresh token not found");
                }
            } else {
                throw new RefreshTokenException(refreshToken, "Token is not valid");
            }
        } catch (Exception ex) {
            String tokenExceptionMessage = jwtUtils.logTokenExceptionMessage(ex);

            throw new RefreshTokenException(refreshToken, tokenExceptionMessage);
        }
    }

    @Override
    public SimpleResponse logout(String userName) {

        refreshTokenCache.remove(userName);

        return new SimpleResponse("User " + userName + " has been logged out!");
    }

    @Override
    public RegDto getRegDtoWithEncryptedPassword(RegDto regDto) {
        regDto.setPassword1(passwordEncoder.encode(regDto.getPassword1()));
        regDto.setPassword2(null);

        return regDto;
    }

    @Override
    public boolean doPasswordsMatch(RegDto regDto) {
        if (StringUtils.hasText(regDto.getPassword1())) {
            if (regDto.getPassword1().equals(regDto.getPassword2())) {
                log.info("password1 matches password2");
                return true;
            }
        }
        log.info("password1 doesn't match password2");

        return false;
    }

    private JwtResponse generateJwtResponse(UserDetails userDetails) {
        String accessToken = jwtUtils.generateAccessToken(userDetails);

        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        refreshTokenCache.put(userDetails.getUsername(), refreshToken);

        JwtResponse jwtResponse =
                JwtResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();

        return jwtResponse;
    }
}
