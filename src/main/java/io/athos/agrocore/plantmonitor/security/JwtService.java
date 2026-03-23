package io.athos.agrocore.plantmonitor.security;

import io.athos.agrocore.plantmonitor.errors.InvalidBearerTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;



@Service
public class JwtService {

    @Value("${SECRET:minha-senha-super-secreta-dois-mil}")
    private String SECRET;
    private static final int EXPIRATION_TIME = 1000 * 60 * 30; // 30 minutos
    private static final int REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 48;
    ;

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(getExpirationDate())
                .claim("tokenType", "ACCESS") // ← aqui
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .claim("token_type", "REFRESH") // ← aqui
                .expiration(getExpirationDateForRefreshToken())
                .signWith(getSecretKey())
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isRefreshToken(String token) {
        String type = getPayload(token).get("token_type", String.class);
        return "REFRESH".equals(type);
    }

    public boolean isAccessToken(String token) {
        String type = getPayload(token).get("token_type", String.class);
        return "ACCESS".equals(type);
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new InvalidBearerTokenException("Invalid Token!");
        }

    }


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    }

    private Date getExpirationDateForRefreshToken() {
        return new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME);
    }

    public String extractJti(String refreshToken) {
        return getPayload(refreshToken).getId();
    }

    private Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())   // ou byte[] secret
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // TODO: make Cron Tab to delete tokens

    public LocalDateTime extractExpirationDate(String refreshToken) {
        return Instant.ofEpochMilli(getPayload(refreshToken).getExpiration().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}

