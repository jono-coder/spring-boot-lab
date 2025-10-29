package com.jono.security.service;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Profile("dev")
public class UserService implements UserDetailsService {

    public record UserDeets(String pwd, List<SimpleGrantedAuthority> roles) {}

    private static final Map<String, UserDeets> USERS = Map.of(
            "admin", new UserDeets("{noop}admin123", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))),
            "jono", new UserDeets("{noop}password", List.of(
                    new SimpleGrantedAuthority("ROLE_USER")))
    );

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final var user = USERS.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new User(username, user.pwd, user.roles);
    }

}
