package project.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.GuardianDto;
import project.model.dto.user.PatientDto;
import project.model.entity.user.GuardianEntity;
import project.model.entity.user.PatientEntity;
import project.model.repository.user.GuardianRepository;
import project.model.repository.user.PatientRepository;
import project.util.JwtUtil;

import java.security.Guard;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private  final PatientRepository patientRepository;

    // [*] JWT 객체 주입 (토큰에 사용)
    private  final JwtUtil jwtUtil;

    private  int loginGno = 0;
    // [1] 보호자 회원가입
    public int signup(GuardianDto guardianDto){
        System.out.println("GuardianService.signup");
        System.out.println("guardianDto = " + guardianDto);

        // 유효성 검사를 위한 보호자 목록 조회
        List<GuardianDto> checkDto = guardianRepository.findAll().stream()
                .map(GuardianEntity::toDto)
                .collect(Collectors.toList());
        // 유효성 검사를 위한 환자 목록 조회 (전화번호 중복 확인용)
        List<PatientDto> checkDto2 = patientRepository.findAll().stream()
                .map(PatientEntity::toDto)
                .collect(Collectors.toList());

        // 유효성 검사
        int validationResult = validateGuardian(guardianDto, checkDto, checkDto2);
        if (validationResult != 0) {
            return validationResult; // 유효성 검사 실패 시 해당
        }

        // 비밀번호 암호화 (비클립트 사용)
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashPwd = bCryptPasswordEncoder.encode(guardianDto.getGpwd());
        guardianDto.setGpwd(hashPwd);

        GuardianEntity guardianEntity = guardianDto.toEntity();
        GuardianEntity saveEntity = guardianRepository.save(guardianEntity);

        if(saveEntity.getGno() > 0) {
            return  saveEntity.toDto().getGno();
        }

        return  0; // 관리자 문의

    }

    // [2] 로그인
    public String login(GuardianDto guardianDto){
        System.out.println("GuardianService.login");
        System.out.println("guardianDto = " + guardianDto);

        GuardianEntity guardianEntity = guardianRepository.findByGid(guardianDto.getGid());
        if(guardianEntity == null){
            return  null;
        }

        // 암호화된 비밀번호 검증
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean inMatches =
                bCryptPasswordEncoder.matches(guardianDto.getGpwd(), guardianEntity.getGpwd());
        if(inMatches == false){
            return  null;
        }
        loginGno = guardianEntity.getGno();

        // 토큰 발급
        String token = jwtUtil.createToken(guardianEntity.getGid());
        System.out.println("로그인 시 발급 된 토큰 확인 : " + token);

        return token;
    }

    // [4] 로그인 된 보호자 정보 조회
    public GuardianDto findInfo(String token){
        System.out.println("GuardianService.findInfo");
        System.out.println("token = " + token);

        // 토큰 검증
        String id = jwtUtil.validateToken(token);

        if(id == null){
            return  null;
        }

        // 정보 조회
        GuardianEntity guardianEntity = guardianRepository.findByGid(id);
        if(guardianEntity == null){
            return null;
        }

        return  guardianEntity.toDto();
    }

    // [5] 보호자 수정
    public int update(GuardianDto guardianDto){
        System.out.println("GuardianService.update");
        System.out.println("guardianDto = " + guardianDto);
//
//        Optional<GuardianEntity> optional = guardianRepository.findById(guardianDto.getGno());
//        if(optional.isEmpty()){
//            return  false;
//        }
//        GuardianEntity guardianEntity = optional.get();

//        // 비밀번호 검증
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        if(!bCryptPasswordEncoder.matches(guardianDto.getGpwd(), guardianEntity.getGpwd())){
//            return  false;
//        }


        // 수정
        int result = guardianRepository.updateGuardian(
                guardianDto.getGname(),
                guardianDto.getGemail(),
                guardianDto.getGphone(),
                guardianDto.getGno()
        );

        return result;
    }

    // [6] 탈퇴
    public boolean delete(int gno, String gpwd){
        System.out.println("GuardianService.delete");
        System.out.println("gno = " + gno + ", gpwd = " + gpwd);

        Optional<GuardianEntity> optional = guardianRepository.findById(gno);
        if(optional.isEmpty()){
            return false;
        }
        GuardianEntity guardianEntity = optional.get();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if(!bCryptPasswordEncoder.matches(gpwd, guardianEntity.getGpwd())){
            return false;
        }

        guardianRepository.deleteById(guardianEntity.getGno());
        return !guardianRepository.existsById(guardianEntity.getGno()); // 존재 여부 확인 후 없으면 true 반환
    }

    // [7] 로그인된 gno 찾기
    public int findGno(){
        System.out.println("GuardianService.findGno");

        return loginGno;
    }


    // [#] 회원가입 유효성 검사
    private int validateGuardian(GuardianDto guardianDto, List<GuardianDto> checkDto,List<PatientDto> patientDto) {
        // 아이디 중복 검사
        if (checkDto.stream().anyMatch(dto -> dto.getGid().equals(guardianDto.getGid()))) {
            return -1; // ID 중복
        }

        // 이메일 중복 검사
        if (checkDto.stream().anyMatch(dto -> dto.getGemail().equals(guardianDto.getGemail()))) {
            return -2; // 이메일 중복
        }

        // 휴대폰 중복 검사
        if (checkDto.stream().anyMatch(dto -> dto.getGphone().equals(guardianDto.getGphone()))
        || patientDto.stream().anyMatch(dto -> dto.getPphone().equals(guardianDto.getGphone()))) {
            return -3; // 전화번호 중복
        }

        // 휴대폰 길이 검사
        if (guardianDto.getGphone().length() != 11) {
            return -4; // 전화번호 길이 오류
        }

        // 아이디 , 비밀번호 길이 검사
        if (guardianDto.getGpwd().length() < 3 || guardianDto.getGpwd().length() > 15 ||
                guardianDto.getGid().length() < 3 || guardianDto.getGid().length() > 15) {
            return -5; // 아이디 , 비밀번호 길이 오류
        }

        return 0; // 모든 유효성 검사 통과
    }

}
