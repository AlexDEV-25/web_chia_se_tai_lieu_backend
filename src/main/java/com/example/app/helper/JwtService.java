package com.example.app.helper;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secretKey}")
	private String secret;

	public String extractUsername(String token) {

		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody().getSubject();
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

		byte[] keyBytes = Decoders.BASE64.decode(secret);

		return Keys.hmacShaKeyFor(keyBytes);
	}
}
