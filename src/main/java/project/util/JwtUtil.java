package project.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import project.model.dto.user.PatientDto;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    // [*] 비밀키 (HS256 알고리즘 사용)
    private Key securityKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // [*] Redis 객체 선언
    @Autowired
    private StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

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

    // [3] 클라이언트 서버로부터 전달 받은환자의 현재 위치 Redis 에 저장
    public boolean saveLocation(PatientDto patient) {
        String key = "location" + patient.getPno();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(patient); // 직렬화

            // Redis에 저장 (24시간 유효)
            stringRedisTemplate.opsForValue().set(key, value, 24, TimeUnit.HOURS);

            return true;  // 저장 성공
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false; // 직렬화 실패
        }
    }


}
