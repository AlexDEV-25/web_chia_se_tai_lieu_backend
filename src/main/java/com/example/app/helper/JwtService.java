package com.example.app.helper;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secretKey}")
	private String secret;

	public String extractUsername(String token) {

		return extractAllClaims(token).getSubject();
	}

	public Claims extractAllClaims(String token) {

		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	public boolean isTokenValid(String token) {

		try {

			Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	private Key getSignKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
}