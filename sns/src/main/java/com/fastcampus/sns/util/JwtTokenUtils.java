package com.fastcampus.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

	private static Claims extractClaims(String token, String key) {
		return Jwts.parserBuilder().setSigningKey(getKey(key))
			.build().parseClaimsJws(token).getBody();
	}


	public static String generateToken(String userName, String key, long expiredTimeMs) { //유저네임,암호화키,유효기간
		Claims claims = Jwts.claims();
		claims.put("userName", userName);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
			.signWith(getKey(key), SignatureAlgorithm.HS256) //해시 256 바이트로 암호화
			.compact();
	}

	public static Key getKey(String key) {
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static Boolean validate(String token, String userName, String key) {
		String usernameByToken = getUsername(token, key);
		return usernameByToken.equals(userName) && !isTokenExpired(token, key);
	}

	public static String getUsername(String token, String key) {
		return extractAllClaims(token, key).get("userName", String.class);
	}

	public static Claims extractAllClaims(String token, String key) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey(key))
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private static Key getSigningKey(String secretKey) {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static Boolean isTokenExpired(String token, String key) {
		Date expiration = extractAllClaims(token, key).getExpiration();
		return expiration.before(new Date());
	}
}
