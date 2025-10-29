package com.jono.security.controller;

import com.jono.security.model.LoginRequest;
import com.jono.security.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(final JwtService jwtService, final UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody final LoginRequest request) {
        final var userDetails = userDetailsService.loadUserByUsername(request.username());
        if (userDetails == null || !userDetails.getPassword().equals("{noop}" + request.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final var access = jwtService.generateAccessToken(userDetails.getUsername(), userDetails.getAuthorities());
        final var refresh = jwtService.generateRefreshToken(userDetails.getUsername(), userDetails.getAuthorities());
        return Map.of("access_token", access, "refresh_token", refresh);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody final Map<String, String> body) {
        final var oldRefresh = body.get("refresh_token");
        final var username = jwtService.validateRefreshToken(oldRefresh);

        final var userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("error", "User not found"));
        }

        final var newAccess = jwtService.generateAccessToken(username, userDetails.getAuthorities());
        return ResponseEntity.ok(Map.of("access_token", newAccess));
    }

}
