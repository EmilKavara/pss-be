package com.pss.pss_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractUsernameFromPayload(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        System.out.println("Generated JWT before signing: " + extraClaims + ", " + userDetails.getUsername());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        System.out.println("Username from token: " + username);
        return true;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String jwt) {
        System.out.println("JWT Token: " + jwt);
        String token = jwt.trim(); // Remove any leading/trailing spaces
        String[] jwtParts = jwt.split("\\.");
        System.out.println("JWT Header (encoded): " + jwtParts[0]);
        System.out.println("JWT Payload (encoded): " + jwtParts[1]);
        System.out.println("JWT Signature (encoded): " + jwtParts[2]);

        // Decode the header and payload using Base64 URL-safe decoding
        String headerDecoded = new String(Base64.getUrlDecoder().decode(jwtParts[0])); // URL-safe decoding
        String payloadDecoded = new String(Base64.getUrlDecoder().decode(jwtParts[1])); // URL-safe decoding

        // Debugging: Print the decoded header and payload
        System.out.println("Decoded Header (JSON): " + headerDecoded);
        System.out.println("Decoded Payload (JSON): " + payloadDecoded);

        System.out.println("Using signing key: " + secretKey);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes()); // Use the injected secretKey here
        //System.out.println("Signing Key (Base64 Encoded): " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        System.out.println("Decoded Claims: " + claims);
        return claims;
    }

    private Claims getAllClaimsFromToken(String token) {
        SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        Claims claims = Jwts.parser()
                .verifyWith(secret)
                .build() // <----
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Utility method to convert a hex string to a byte array
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String extractUsernameFromPayload(String token) {
        try {
            String[] jwtParts = token.split("\\.");
            if (jwtParts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            // Decode the payload (second part of the JWT)
            String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]));
            System.out.println("Decoded Payload: " + payload);

            // Extract the username from the payload
            // This assumes the payload contains a "sub" field (subject) for the username
            int start = payload.indexOf("\"sub\":\"") + 7;
            int end = payload.indexOf("\"", start);
            if (start > 6 && end > start) {
                return payload.substring(start, end);
            }
            throw new IllegalArgumentException("Username not found in payload");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract username from token", e);
        }
    }

}