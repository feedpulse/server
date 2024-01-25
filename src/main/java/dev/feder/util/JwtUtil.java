package dev.feder.util;

import dev.feder.model.SpringUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${feedpulse.server.jwtSecret}")
    private String jwtSecret;

    @Value("${feedpulse.server.jwtExpirationMs}")
    private int jwtExpirationMs;

    private static final String SECRET = "your-secret-key"; // Replace with a secure secret key
    private final long EXPIRATION_TIME = 10_800_000; // 3 hours

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(key()).parseClaimsJws(token).getBody();
    }

    public String generateToken(@NonNull String username, Map<String, Object> claims) {
        if (claims == null) claims = new HashMap<>();
        return createToken(claims, username);
    }

    public String generateToken(@NonNull UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(Authentication authentication) {
        SpringUserDetails user = (SpringUserDetails) authentication.getPrincipal();
        return generateToken(user);
    }

    public String generateToken(SpringUserDetails springUserDetails) {
        Map<String, Object> claims = Collections.singletonMap("roles", springUserDetails.getAuthorities());
        return generateToken(springUserDetails.getUsername(), claims);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Additional utility methods...
}

