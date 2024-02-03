package ru.socialnet.team43.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.service.UserService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthDto authData =
                userService
                        .findUserByEmail(username)
                        .orElseThrow(
                                () ->
                                        new UsernameNotFoundException(
                                                "User not found. Username is: " + username));

        return new AppUserDetails(authData);
    }
}
