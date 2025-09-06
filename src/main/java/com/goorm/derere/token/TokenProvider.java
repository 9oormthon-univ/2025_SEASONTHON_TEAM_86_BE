package com.goorm.derere.token;

import io.jsonwebtoken.*;
//import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TokenProvider: JWT 생성, 파싱, 인증객체 추출, 유효성 검사 등의 토큰 관련 핵심 로직을 담당
 */

@Component
@RequiredArgsConstructor

public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;
    private static final String KEY_ROLE = "role";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7; // 7일
    private final long EXPIRATION_TIME = 1000 * 60 * 60;
    // secretKey를 Key 객체로 변환
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(secretKey.getBytes()).getBytes());
    }

    // AccessToken 생성
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 토큰 생성 - 사용자 정보와 권한을 기반으로 JWT 생성
    private String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(","));

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("nickname");

        String role = oAuth2User.getAttribute("role");
        if (role == null) {
            role = "HOST"; // 기본값 지정
        }

        return Jwts.builder()
                .setSubject(email)       // 사용자 식별자 (userId)
                .claim("name", name)          // 사용자 권한 정보 저장
                .setIssuedAt(now)                           // 발급시간
                .setExpiration(expiry)                      // 만료시간
                .signWith(key, SignatureAlgorithm.HS256)    // 서명
                .compact();
    }


    // JWT에서 사용자 인증 객체 추출
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // 👇 추가: 인증 토큰이 아니면 예외 던지기
        Object tokenType = claims.get("tokenType");
        if (tokenType == null || !"access".equals(tokenType.toString())) {
            throw new IllegalArgumentException("This token is not an access token.");
        }

        // 👇 추가: role null 방지
        Object roleObj = claims.get(KEY_ROLE);
        String rawRole = roleObj != null ? roleObj.toString() : "ROLE_USER";

        List<SimpleGrantedAuthority> authorities = Arrays.stream(rawRole.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }


    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 Claims 추출
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // HttpServletRequest 헤더에서 Authorization 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createTravelToken(String userId, int travelId, String role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("travelId", travelId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }



    // 디코딩용 메서드도 필요
    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
    }

}

