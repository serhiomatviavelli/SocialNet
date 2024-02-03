package ru.socialnet.team43.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.socialnet.team43.dto.UserAuthDto;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {
    private final UserAuthDto authData;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority grantedAuthority =
                new SimpleGrantedAuthority("ROLE_" + authData.getRole().name());
        return Collections.singleton(grantedAuthority);
    }

    @Override
    public String getPassword() {
        return authData.getPassword();
    }

    @Override
    public String getUsername() {
        return authData.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
