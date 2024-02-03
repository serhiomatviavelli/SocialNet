package ru.socialnet.team43.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${app.jwt.accessTokenLifeTime}")
    private Duration accessTokenLifetime;

    @Value("${app.jwt.refreshTokenLifeTime}")
    private Duration refreshTokenLifetime;

    private SecretKey accessKey;
    private SecretKey refreshKey;

    public String generateAccessToken(UserDetails userDetails) {
        JwtBuilder tokenBuilder = tokenCommonDataBuilder(userDetails, accessTokenLifetime);
        
        return tokenBuilder
                .claim("roles", userDetails.getAuthorities())
                .signWith(getAccessKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        JwtBuilder tokenBuilder = tokenCommonDataBuilder(userDetails, refreshTokenLifetime);

        return tokenBuilder.signWith(getRefreshKey()).compact();
    }

    private JwtBuilder tokenCommonDataBuilder(UserDetails userDetails, Duration lifetime) {
        Date issueTime = new Date();
        Date expirationTime = new Date(issueTime.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .header()
                .empty()
                .add("typ", "JWT")
                .and()
                .subject(userDetails.getUsername())
                .issuedAt(issueTime)
                .expiration(expirationTime);
    }

    public boolean isAccessTokenValid(String accessToken) {
        boolean isAccessTokenValid = false;
        try {
            isAccessTokenValid = isTokenValid(accessToken, getAccessKey());
        } catch (Exception ex) {
            logTokenExceptionMessage(ex);
        }

        return isAccessTokenValid;
    }

    public boolean isRefreshTokenValid(String refreshToken) throws Exception {
        return isTokenValid(refreshToken, getRefreshKey());
    }

    public String getUserNameFromAccessToken(String accessToken) {
        return getUserNameFromToken(accessToken, getAccessKey());
    }

    public String getUserNameFromRefreshToken(String refreshToken) {
        return getUserNameFromToken(refreshToken, getRefreshKey());
    }

    private String getUserNameFromToken(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private boolean isTokenValid(String token, SecretKey secretKey) throws Exception {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

        return true;
    }

    public String logTokenExceptionMessage(Exception ex) {
        String returningMessage = "";
        String logMessageSuffix = ": {}";
        if (ex instanceof SignatureException) {
            returningMessage = "Invalid signature";
        } else if (ex instanceof MalformedJwtException) {
            returningMessage = "Invalid token";
        } else if (ex instanceof ExpiredJwtException) {
            returningMessage = "Token is expired";
        } else if (ex instanceof UnsupportedJwtException) {
            returningMessage = "Token is unsupported";
        } else if (ex instanceof IllegalArgumentException) {
            returningMessage = "Claims string is empty";
        } else {
            logMessageSuffix = "";
        }

        log.error(returningMessage + logMessageSuffix, ex);

        return returningMessage;
    }

    public SecretKey generateKey(String secretName) {
        SecretKey key = Jwts.SIG.HS512.key().build();
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        log.debug(secretName + " : " + secretString);

        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

        return key;
    }

    private SecretKey getAccessKey() {
        if (accessKey == null) {
            accessKey = generateKey("Access secret");
        }

        return accessKey;
    }

    private SecretKey getRefreshKey() {
        if (refreshKey == null) {
            refreshKey = generateKey("Refresh secret");
        }

        return refreshKey;
    }
}
