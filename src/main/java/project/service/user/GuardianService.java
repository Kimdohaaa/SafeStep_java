package project.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.GuardianDto;
import project.model.entity.user.GuardianEntity;
import project.model.repository.user.GuardianRepository;
import project.util.JwtUtil;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;

    // [*] JWT 객체 주입 (토큰에 사용)
    private  final JwtUtil jwtUtil;

    private  int loginGno = 0;
    // [1] 보호자 회원가입
    public GuardianDto signup(GuardianDto guardianDto){
        System.out.println("GuardianService.signup");
        System.out.println("guardianDto = " + guardianDto);
        
        // 유효성 검사 추가 해야 함

        // 비밀번호 암호화 (비클립트 사용)
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashPwd = bCryptPasswordEncoder.encode(guardianDto.getGpwd());
        guardianDto.setGpwd(hashPwd);

        GuardianEntity guardianEntity = guardianDto.toEntity();
        GuardianEntity saveEntity = guardianRepository.save(guardianEntity);

        if(saveEntity.getGno() > 0) {
            return  saveEntity.toDto();
        }

        return  null;

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
    public boolean update(GuardianDto guardianDto){
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

        return result > 0;
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

}
