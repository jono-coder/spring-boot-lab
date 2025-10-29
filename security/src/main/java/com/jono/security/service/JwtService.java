package com.jono.security.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessValidity;
    private final long refreshValidity;

    protected JwtService(@Value("${jwt.secret}") final String secret,
                         @Value("${jwt.access-token-validity}") final long accessValidity,
                         @Value("${jwt.refresh-token-validity}") final long refreshValidity) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessValidity = accessValidity;
        this.refreshValidity = refreshValidity;
    }

    public String generateAccessToken(final String username, final Collection<? extends GrantedAuthority> roles) {
        final var now = Instant.now();
        final var roleNames = roles.stream()
                                   .map(GrantedAuthority::getAuthority)
                                   .toList();
        return Jwts.builder()
                   .subject(username)
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(now.plusSeconds(accessValidity)))
                   .claim("type", "access")
                   .claim("roles", roleNames)
                   .signWith(key)
                   .compact();
    }

    public String generateRefreshToken(final String username, final Collection<? extends GrantedAuthority> roles) {
        final var now = Instant.now();
        final var roleNames = roles.stream()
                                   .map(GrantedAuthority::getAuthority)
                                   .toList();
        return Jwts.builder()
                   .subject(username)
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(now.plusSeconds(refreshValidity)))
                   .claim("type", "refresh")
                   .claim("roles", roleNames)
                   .signWith(key)
                   .compact();
    }

    public String validateRefreshToken(final String token) {
        final var claims = Jwts.parser()
                               .verifyWith(key)
                               .build()
                               .parseSignedClaims(token)
                               .getPayload();

        if (!"refresh".equals(claims.get("type"))) {
            throw new JwtException("Invalid token type");
        }
        return claims.getSubject();
    }

}
