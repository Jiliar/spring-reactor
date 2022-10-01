package com.jsolution.security;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//Clase S4
@Component
public class JWTUtil implements Serializable {

    @Value("${jjwt.secret}")
    private String secret;

    @Value("${jjwt.expiration}")
    private String expirationTime;

    public Claims getAllClaimsFromToken(String token){
        var secret_key = this.secret.isEmpty() ? System.getenv("JWT_SECRET").getBytes() : this.secret.getBytes();
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(secret_key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token){
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(AuthUser user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("username", user.getUsername());
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String username){
        var expirationTimes = ! System.getenv("JWT_EXPIRATION").isEmpty()? System.getenv("JWT_EXPIRATION") : expirationTime;
        Long expirationTimeLong =  Long.parseLong(expirationTimes);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000); //aprox 46min


        var secret_key = ! System.getenv("JWT_SECRET").isEmpty() ? System.getenv("JWT_SECRET").getBytes() : this.secret.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(secret_key);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
