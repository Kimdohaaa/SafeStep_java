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

import java.util.ArrayList;
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

        // DB 에 저장된 안전 위치 조회
        Optional<PatientEntity> saveLocation = Optional.ofNullable(patientRepository.findSaveLocation(patientDto.getPno()));
        if (saveLocation.isPresent()) {
            PatientDto findPatientDto = saveLocation.get().toDto();
            double savePlon = findPatientDto.getPlon();
            double savePlat = findPatientDto.getPlat();

            // 위치 비교를 위해 현재 환자의 위치정보와 조회한 안전 위치 정보 매개변수로 전달
            return jwtUtil.saveLocation(patientDto, savePlon, savePlat);
        } else {
            // 안전 위치가 없는 경우 처리
            System.out.println("환자 정보 없음");
            return false;
        }
    }

    // [4] 환자의 이동경로 요청
    public List<PatientDto> findRoute(int pno){
        System.out.println("LocationService.findRoute");
        System.out.println("pno = " + pno);

        System.out.println("위치정보 조회");
        return jwtUtil.findRoute(pno);
    }

    // [5] 클라이언트 서버가 요청한 pno 에 해당하는 레디스 내 최신 위치정보 반환
    public List<PatientDto> getLastLocations( List<Integer> pnoList){
        System.out.println("LocationService.getLastLocations");
        System.out.println("pnoList = " + pnoList);

        return  jwtUtil.getLastLocations(pnoList);
    }

    // [6] 앱 실행 FCM 토큰을 발급받아서 레디스에 저장
    public boolean saveFcmToken(@RequestParam int gno , @RequestParam String fcmToken){
        System.out.println("LocationController.saveFcmToken");
        System.out.println("Gno = " + gno + ", fcmToken = " + fcmToken);

        // 해당 보호자가 관리중인 환자 번호 가져오기
        List<PatientDto> patientList = patientRepository.findByGno(gno).stream()
                .map(PatientEntity::toDto)
                .collect(Collectors.toList());

        List<Integer> pnoList = new ArrayList<>();
        for (PatientDto patient : patientList) {
            pnoList.add(patient.getPno()); // getpno() 메서드 호출
        }

        return jwtUtil.saveFcmToken(gno, fcmToken, pnoList);
    }

    // [7] 클라이언트 서버의 요청 시 푸시 알림을 보내기 위해 토큰 반환하기
    public String findFcmToken(int pno){
        System.out.println("LocationController.findFcmToken");
        System.out.println("pno = " + pno);

        // 해당 환자에 해당하는 gno 가져오기
        PatientEntity patientEntity = patientRepository.findPatientByGno(pno);
        if(patientEntity != null){
            int gno = patientEntity.getGno();

            System.out.println("조회한 보호자 번호 : " + gno);
            String fcmToken = jwtUtil.findFcmToken(gno);

            return  fcmToken;
        }
        return  null;
    }
}
