package project.service.location;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import project.model.dto.user.PatientDto;
import project.model.entity.user.PatientEntity;
import project.model.repository.user.PatientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {
    private final PatientRepository patientRepository;

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


    // [2] 현재 로그인된 Gno 에 등록된 환자들의 위치 정보 조회
    public List<PatientDto> findLocation( int gno){
        System.out.println("LocationService.findLocation");
        System.out.println("gno = " + gno);

        return patientRepository.findLocation(gno).stream()
                .map(PatientEntity::toDto)
                .collect(Collectors.toList());
    }
}
