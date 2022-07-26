package com.example.godtudy.global.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Setter
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secretKey}")
    private String secretKey;

    public static final long TOKEN_VALID_TIME = 3 * 60 * 60 * 1000L; // 30분
//    public static final long TOKEN_VALID_TIME = 3 * 60 * 1000L;
    public static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //토큰으로부터 아이디가져오기
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //토큰으로부터 날짜 가져오기
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰이 만료되었는지 확인(true - 만료)
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
        } catch (IllegalArgumentException e) {
            System.out.println("an error occured during getting username from token");
            e.printStackTrace();
            return false;
        } catch(SignatureException e){
            System.out.println("Authentication Failed. Username or Password not valid.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //accesstoken 발급
    public String createAccessToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);

        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_TIME)).compact();
    }

    //refresh token 발급
    public String createRefreshToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME)).compact();
    }



}
