package com.authentication.authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET_KEY = "a179ec8ec555c1f30c5bb447cf50eddda94f99b41162bd0436396c343a303f65dfe5da9f50b27c62ff3a574e140826f4c3c3bbcf5935b591635f141361b1413f6eec6584c63d48decd81a3b03e14793a6eb9a0e7316a3289e7f602e981bfe12cba9be887db528bc3f5fc0e7640c7a565f575f726c0ca0fe2b24b1c9d786f5acdd368b888108bcf8de14600e973604e5dfe1ce92cee14640edcd7979b371510d5f974def33670650d41631f1b2b7200a28a6fbe8b197e57e491f14d41f295dd1fb574f5ea47d0def2b4d1b036bcf8ed416600ecc8402464c65168fa82f3529e3d005c69a9552f9a64619c5fcb5d53d13c91323c3cab8026eea36bb63d8797befc";

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private Key getSigningKey() {
        byte[] keyBytes = hexStringToByteArray(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractUserEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long expirationTimeMs = 1000 * 60 * 60 * 24 * 14;
        return Jwts.builder()
                .setClaims(extraClaims != null ? extraClaims : Map.of())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
