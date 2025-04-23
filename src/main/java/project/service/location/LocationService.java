package project.service.location;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import project.model.dto.user.PatientDto;
import project.model.entity.user.PatientEntity;
import project.model.repository.user.PatientRepository;
import project.util.JwtUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {
    private final PatientRepository patientRepository;
    private final JwtUtil jwtUtil;

    // [1] 환자 안전 위치 최초 등록
    public boolean enrollLocation(PatientDto patientDto){
        System.out.println("LocationService.enrollLocation");
        System.out.println("patientDto = " + patientDto);

        PatientEntity patientEntity = patientDto.toEntity();

        int result = patientRepository.enrollLocation(
                patientEntity.getPlon(),
                patientEntity.getPlat(),
                patientEntity.getPno()
        );

        if(result > 0){
            return  true;
        }

        return  false;
    }

    // [2] 환자의 휴대폰번호를 통해 인증 및 pno 조회
    public Integer findPno(String pphone){
        System.out.println("LocationService.findPno");
        System.out.println("pphone = " + pphone);

        return patientRepository.findPno(pphone);
    }

    // [3] 클라이언트 서버로부터 전달 받은환자의 현재 위치 Redis 에 저장
    public boolean saveLocation(PatientDto patientDto){
        System.out.println("LocationService.saveLocation");
        System.out.println("patientDto = " + patientDto);

        return jwtUtil.saveLocation(patientDto);
    }


}
