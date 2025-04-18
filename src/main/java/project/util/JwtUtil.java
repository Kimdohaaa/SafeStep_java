package project.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // [*] 비밀키 (HS256 알고리즘 사용)
    private Key securityKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);



    // [1] JWT 토큰 발급
    public String createToken(String gid){
        String token = Jwts.builder()
                .setSubject(gid) // 토큰에 보호자의 id 등록
                .setIssuedAt(new Date()) // 토큰 발급 날짜 현재로 지정
                .signWith(securityKey) // 비밀키 암호화
                .compact(); // 토큰 생성

        return token;
    }

    // [2] JWT 토큰 검증
    public  String validateToken(String token){
        try{
            Claims claims = Jwts.parser() // 토큰 검증
                    .setSigningKey(securityKey) // 검증하기 위한 비밀키 지정
                    .build() // 검증
                    .parseClaimsJws(token) // 검증할 토큰 해석
                    .getBody(); // 검증된 토큰 객체 생성 후 반환

            System.out.println("토큰 확인 : " + claims.getSubject());

            return claims.getSubject();

        }catch (ExpiredJwtException e){
            System.out.println("토큰 만료 : " + e);
        }catch (JwtException e){
            System.out.println("JWT 예외 발생 : " + e);
        }

        return  null;
    }

}
