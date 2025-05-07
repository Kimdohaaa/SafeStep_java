package project.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import project.model.dto.user.GuardianDto;
import project.model.dto.user.PatientDto;

import java.security.Key;
import java.time.Duration;
import java.util.ArrayList;
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
    public boolean saveLocation(PatientDto patient, double savePlon, double savePlat) {
        String key = "location" + patient.getPno();
        String stateKey = "state" + patient.getPno(); // 안전 상태를 저장할 키
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 안전 반경 내인 지 검사
            double distance = calculateDistance(
                    patient.getPlon(), patient.getPlat(),
                    savePlon, savePlat
            );

            // 150 이내면 true 아니면 false
            boolean currentState = distance <= 150;
            patient.setPstate(currentState);

            // 직렬화
            String value = objectMapper.writeValueAsString(patient);

//            stringRedisTemplate.delete("location1"); // 환자 번호에 맞게 location{pno} 형태로바꿔야됨

            // Redis에 저장 (24시간 유효)
            stringRedisTemplate.opsForList().rightPush(key, value);
            stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);

            // stringRedisTemplate.opsForValue().set(key, value, 24, TimeUnit.HOURS);


            // Redis 저장 확인
            List<String> redisList = stringRedisTemplate.opsForList().range(key, -1, -1);
            if (redisList != null && !redisList.isEmpty()) {
                System.out.println("레디스에 저장된 값 " + redisList.get(0));
            }

            return currentState;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false; // 직렬화 실패
        }
    }

    // [#] 거리 계산 메소드
    // plon, plat : 클라이언트에서 보낸 환자의 현재 위치
    // savePlon, savePlat : DB 에 저장된 환자의 안전 위치
    public double calculateDistance(double plon, double plat, double savePlon, double savePlat) {
        final int R = 6371000; // 지구의 반지름 (단위: 미터). 거리 계산을 위해 사용됨

        // 위도 차이를 라디안 단위로 변환 (위도는 북쪽/남쪽 방향 거리 차이)
        double dLat = Math.toRadians(savePlat - plat);

        // 경도 차이를 라디안 단위로 변환 (경도는 동쪽/서쪽 방향 거리 차이)
        double dLon = Math.toRadians(savePlon - plon);


        // 두 위치 간의 중심 각(angular distance) 구하기
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(plat)) * Math.cos(Math.toRadians(savePlat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        // 중심 각을 거리로 변환 (대각선 거리 구함)
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 최종 거리 계산 : 중심각 c에 지구 반지름 R을 곱해 실제 거리(m)로 환산
        return R * c;
    }


    // [4] 안전 위치를 벗어난 환자의 이동 경로조회
    public List<PatientDto> findRoute(int pno){
        System.out.println("JwtUtil.findRoute");
        System.out.println("pno = " + pno);

        String key = "location" + pno;
        ObjectMapper objectMapper = new ObjectMapper();
        List<PatientDto> result = new ArrayList<>();

        // Redis 리스트에 저장된 모든 위치 데이터 가져오기
        List<String> redisDataList = stringRedisTemplate.opsForList().range(key, 0, -1);

        if (redisDataList != null) {
            for (String item : redisDataList) {
                try {
                    PatientDto patientDto = objectMapper.readValue(item, PatientDto.class);

                    // 같은 환자 번호 && pstate == false
                    if (patientDto.getPno() == pno && patientDto.isPstate() == false) {
                        result.add(patientDto);
                    }
                } catch (JsonProcessingException e) {
                    System.out.println(e);
                }
            }
        }

        return result;

    }

    // [5] 클라이언트 서버가 요청한 pno 에 해당하는 레디스 내 최신 위치정보 반환
    public List<PatientDto> getLastLocations(List<Integer> pnoList) {
        List<PatientDto> lastLocations = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Integer pno : pnoList) {
            String key = "location" + pno;

            // Redis에서 해당 환자의 마지막 위치 정보 가져오기
            List<String> values = stringRedisTemplate.opsForList().range(key, -1, -1);

            if (values != null && !values.isEmpty()) {
                String latestValue = values.get(0);
                try {
                    PatientDto patient = objectMapper.readValue(latestValue, PatientDto.class);
                    lastLocations.add(patient);
                } catch (JsonProcessingException e) {
                    System.out.println(e);
                }
            }
        }


        return lastLocations;
    }

    // [6] 앱 실행 FCM 토큰을 발급받아서 레디스에 저장
    public boolean saveFcmToken(int gno, String fcmToken, List<Integer> pnoList ){

        try {
            GuardianDto guardianDto = new GuardianDto();
            guardianDto.setGno(gno);
            guardianDto.setFcmToken(fcmToken);
            guardianDto.setPnoList(pnoList);

            String redisKey = "guardian:" + gno;

            ObjectMapper objectMapper = new ObjectMapper();

            String json = objectMapper.writeValueAsString(guardianDto);
            String key = "guardian:" + gno;

            stringRedisTemplate.opsForValue().set(key, json, Duration.ofHours(24));
            return true;

        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    // [7] 클라이언트 서버의 요청 시 푸시 알림을 보내기 위해 토큰 반환하기
    public  String findFcmToken(int gno){
        String key = "guardian:" + gno;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (json == null) return null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            GuardianDto guardian = objectMapper.readValue(json, GuardianDto.class);
            System.out.println("레디스에서 조회한 정보확인(FCM토큰관련)" + guardian);
            return guardian.getFcmToken();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
