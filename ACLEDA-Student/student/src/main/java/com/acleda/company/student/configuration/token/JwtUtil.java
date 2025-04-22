package com.acleda.company.student.configuration.token;

import com.acleda.company.student.administrator.model.TAppUser;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtUtil {

    SecretKey key = Keys.hmacShaKeyFor("3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b".getBytes());
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    // Method to generate JWT token for a user
    public String generateToken(TAppUser user) {
        return Jwts.builder()
                .setSubject(user.getUsername())  // Set subject (user's username)
                .setIssuedAt(new Date())         // Set issue date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Set expiration date
                .signWith(key, SignatureAlgorithm.HS512)  // Use HS512 algorithm and the key
                .compact();  // Generate and return the token
    }
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // Returns the username from the token
    }
    // Get expiration time from the JWT
    public Date getExpirationTime(String token) {

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)  // Ensure the same secret and algorithm for parsing
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (SignatureException e) {
            // Handle invalid token
            return null;
        }
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationTime(token);
        if (expiration != null) {
            return expiration.before(new Date());
        }
        return true;  // If the expiration could not be parsed, consider it expired
    }
    public boolean validateRefreshToken(String refreshToken) {
        try {
            // Parse the refresh token to check validity
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            return true;
        } catch (JwtException e) {
            return false; // Token is invalid or expired
        }
    }


    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiry
                .signWith(key)
                .compact();
    }

    public long getExpiryForAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime();
    }
}
