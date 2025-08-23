package com.example.Backend.services;

import com.example.Backend.enums.TokenType;
import com.example.Backend.models.RefreshToken;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret.access}")
    private String SECRET_ACCESS;
    @Value("${jwt.secret.refresh}")
    private String SECRET_REFRESH;
    @Value("${jwt.expiration.access}")
    private Long EXPIRATION_ACCESS;
    @Value("${jwt.expiration.refresh}")
    private Long EXPIRATION_REFRESH;
    @Value("${jwt.issuer}")
    private String ISSUER;

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateToken(UserDetails userDetails, String tokenId, TokenType tokenType) {
        String token = Jwts.builder()
                .id(tokenId)
                .claim("type", tokenType.name())
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + getExpiration(tokenType)))
                .signWith(getSecretKey(tokenType))
                .issuer(ISSUER)
                .compact();

        if(tokenType == TokenType.REFRESH_TOKEN) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(tokenId)
                    .refreshToken(token)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return token;
    }

    public Claims extractClaims(String token, TokenType tokenType) {
        return Jwts.parser()
                .verifyWith(getSecretKey(tokenType))
                .requireIssuer(ISSUER)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSecretKey(TokenType tokenType) {
        if (tokenType == TokenType.ACCESS_TOKEN) {
            return Keys.hmacShaKeyFor(SECRET_ACCESS.getBytes(StandardCharsets.UTF_8));
        } else if (tokenType == TokenType.REFRESH_TOKEN) {
            return Keys.hmacShaKeyFor(SECRET_REFRESH.getBytes(StandardCharsets.UTF_8));
        }
        throw new IllegalArgumentException("Invalid token type");
    }

    private long getExpiration(TokenType tokenType) {
        return tokenType == TokenType.ACCESS_TOKEN ? EXPIRATION_ACCESS : EXPIRATION_REFRESH;
    }

    public boolean isExpired(String token, TokenType tokenType) {
        Claims claims = extractClaims(token, tokenType);
        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails, TokenType tokenType) {
        Claims claims = extractClaims(token, tokenType);
        String username = claims.getSubject();
        return (username.equals(userDetails.getUsername()) && !isExpired(token, tokenType));
    }

    public boolean verifyToken(String token, TokenType tokenType) {
        try {
            extractClaims(token, tokenType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractName(String token, TokenType tokenType) {
        Claims claims = extractClaims(token, tokenType);
        return claims.getSubject();
    }


    public String extractTokenId(String jwtToken, TokenType tokenType) {
        Claims claims = extractClaims(jwtToken, tokenType);
        return claims.getId();
    }
}
