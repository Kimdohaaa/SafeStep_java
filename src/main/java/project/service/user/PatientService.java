package project.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.PatientDto;
import project.model.entity.user.PatientEntity;
import project.model.repository.user.PatientRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    // [1] 환자 등록
    public PatientDto enroll(PatientDto patientDto){
        System.out.println("PatientService.enroll");
        System.out.println("patientDto = " + patientDto);

        PatientEntity patientEntity = patientDto.toEntity();
        PatientEntity saveEntity = patientRepository.save(patientEntity);

        if(saveEntity.getPno() > 0){
            return saveEntity.toDto();
        }

        return  null;
    }

    // [2] 특정 보호자의 환자 전체 조회
    public List<PatientDto> findAll(int gno){
        System.out.println("PatientService.findAll");
        System.out.println("gno = " + gno);

        return patientRepository.findByGno(gno).stream()
                .map(PatientEntity::toDto)
                .collect(Collectors.toList());
    }

    // [3] 환자 개별 조회
    public PatientDto find(int pno) {
        System.out.println("PatientService.find");
        System.out.println("pno = " + pno);

        return patientRepository.findById(pno)
                .map(PatientEntity::toDto)
                .orElse(null);
    }

    // [4] 환자 정보 수정
    public boolean update(PatientDto patientDto){
        System.out.println("PatientService.update");
        System.out.println("patientDto = " + patientDto);

        Optional<PatientEntity> optional = patientRepository.findById(patientDto.getPno());
        if(optional.isEmpty()){
            return false;
        }

        PatientEntity patientEntity = optional.get();

        int result = patientRepository.updatePatient(
                patientDto.getPname(),
                patientDto.getPnumber(),
                patientDto.isPgender(),
                patientDto.getPage(),
                patientDto.getPgrade(),
                patientDto.getRelation(),
                patientDto.getPno()
        );
        // 환자 안전 위치 수정 로직은 따로 구현하기 (프론트에서 네이버 맵으로 보낼 거임)

        return result > 0;
    }

    // [5] 환자 정보 삭제
    public boolean delete(int pno){
        System.out.println("PatientService.delete");
        System.out.println("pno = " + pno);

        Optional<PatientEntity> optional = patientRepository.findById(pno);
        if(optional.isEmpty()){
            return false;
        }

        patientRepository.deleteById(pno);
        return !patientRepository.existsById(pno);
    }

}
