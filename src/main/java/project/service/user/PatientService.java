package project.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.GuardianDto;
import project.model.dto.user.PatientDto;
import project.model.entity.user.GuardianEntity;
import project.model.entity.user.PatientEntity;
import project.model.repository.user.GuardianRepository;
import project.model.repository.user.PatientRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private  final GuardianRepository guardianRepository;

    // [1] 환자 등록
    public int enroll(PatientDto patientDto){
        System.out.println("PatientService.enroll");
        System.out.println("patientDto = " + patientDto);

        // 유효성 검사를 위한 환자 목록 조회
        List<PatientDto> checkDto = patientRepository.findAll().stream()
                .map(PatientEntity::toDto)
                .collect(Collectors.toList());
        // 유효성 검사를 위한 보호자 목록 조회 (전화번호 중복 확인용)
        List<GuardianDto> checkDto2 = guardianRepository.findAll().stream()
                .map(GuardianEntity::toDto)
                .collect(Collectors.toList());

        int validationResult = validatePatient(patientDto, checkDto, checkDto2);
        if(validationResult != 0){
            return  validationResult;
        }

        PatientEntity patientEntity = patientDto.toEntity();
        PatientEntity saveEntity = patientRepository.save(patientEntity);

        if(saveEntity.getPno() > 0){
            return  saveEntity.toDto().getGno();
        }

        return  0; // 관리자 문의
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
    public int update(PatientDto patientDto){
        System.out.println("PatientService.update");
        System.out.println("patientDto = " + patientDto);

        Optional<PatientEntity> optional = patientRepository.findById(patientDto.getPno());
        if(optional.isEmpty()){
            return 0;
        }

        PatientEntity patientEntity = optional.get();

        // 유효성 검사를 위한 보호자 목록 조회
        List<GuardianDto> checkDto = guardianRepository.findAll().stream()
                .map(GuardianEntity::toDto)
                .collect(Collectors.toList());
        // 유효성 검사를 위한 환자 목록 조회 (전화번호 중복 확인용)
        List<PatientDto> checkDto2 = patientRepository.findAll().stream()
                .map(PatientEntity::toDto)
                .collect(Collectors.toList());

        // 전화번호 길이 검사
        if (!patientEntity.getPphone().equals(patientDto.getPphone())) {
            if (patientDto.getPphone().length() != 11) {
                return -1;
            }

            boolean duplicate = checkDto.stream().anyMatch(dto -> dto.getGphone().equals(patientDto.getPphone())) ||
                    checkDto2.stream().anyMatch(dto -> dto.getPphone().equals(patientDto.getPphone()));
            if (duplicate) {
                return -3;
            }
        }
        // 나이 유효성 검사
        int page = 0;
        if (patientDto.getPage() != null && !patientDto.getPage().isBlank() &&
                !patientEntity.getPage().equals(patientDto.getPage())) {
            try {
                page = Integer.parseInt(patientDto.getPage());
            } catch (NumberFormatException e) {
                System.out.println("환자 나이 형변환 오류 : " + e);
                return -2;
            }
        }





        int result = patientRepository.updatePatient(
                patientDto.getPname(),
                patientDto.getPnumber(),
                patientDto.isPgender(),
                patientDto.getPage(),
                patientDto.getPgrade(),
                patientDto.getRelation(),
                patientDto.getPno(),
                patientDto.getPphone()
        );
        // 환자 안전 위치 수정 로직은 따로 구현하기 (프론트에서 네이버 맵으로 보낼 거임)

        return result;
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

    // [#] 환자 등록 유효성 검사
    private  int validatePatient(PatientDto patientDto, List<PatientDto> checkDto, List<GuardianDto> guardianCheck){
        // 주민등록번호 유효성 검사
        if(checkDto.stream().anyMatch(dto -> dto.getPnumber().equals(patientDto.getPnumber()))){
            return -1; // 주민등록번호 중복
        }
        // 환자 나이 유효성 검사
        int page = 0;
        try {
            if (patientDto.getPage() != null && !patientDto.getPage().isBlank()) {
                page = Integer.parseInt(patientDto.getPage());
            }
        } catch (NumberFormatException e) {
            System.out.println("환자 나이 형변환 오류 : " + e);
            return -2;  // 예외 발생 시 바로 종료
        }

        if (page <= 0) {
            return -2;
        }
        // 전화번호 중복 검사
        if(checkDto.stream().anyMatch(dto -> dto.getPphone().equals(patientDto.getPphone()))
        || guardianCheck.stream().anyMatch(dto -> dto.getGphone().equals(patientDto.getPphone()))){
            return -3;
        }

        // 전화번호 길이 검사
        if(patientDto.getPphone().length() != 11){
            return -4;
        }

        // 주민등록번호 유효성 검사
        if(patientDto.getPnumber().length() != 13){
            return -5;
        }

        return 0;
    }
}
