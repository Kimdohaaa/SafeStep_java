package project.controller.location;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.PatientDto;
import project.service.location.LocationService;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LocationController {
    private  final LocationService locationService;

    // [1] 환자 안전 위치 최초 등록
    @PostMapping
    public boolean enrollLocation(@RequestBody PatientDto patientDto){
        System.out.println("LocationController.enrollLocation");
        System.out.println("patientDto = " + patientDto);

        return  locationService.enrollLocation(patientDto);
    }

    // [2] 환자의 휴대폰번호를 통해 인증 및 pno 조회
    @PostMapping("/findpno")
    public Integer findPno(@RequestParam String pphone){
        System.out.println("LocationService.findPno");
        System.out.println("pphone = " + pphone);

        Integer pno = locationService.findPno(pphone);
        if(pno != null && pno > 0){
            return  pno;
        }else {
            return 0;
        }
    }

    // [3] 클라이언트 서버로부터 전달 받은환자의 현재 위치 Redis 에 저장
    @PostMapping("/save")
    public boolean saveLocation(@RequestBody PatientDto patientDto){
        System.out.println("LocationController.saveLocation");
        System.out.println("patientDto = " + patientDto);

        System.out.println("위치정보받음");
        return locationService.saveLocation(patientDto);

    }

    // [4] 환자의 이동경로 요청
    @GetMapping("/findroute")
    public List<PatientDto> findRoute(@RequestParam int pno){
        System.out.println("LocationController.findRoute");
        System.out.println("pno = " + pno);

        System.out.println("위치정보 조회");
        return locationService.findRoute(pno);
    }

    // [5] 클라이언트 서버가 요청한 pno 에 해당하는 레디스 내 최신 위치정보 반환
    @PostMapping("/lastroute")
    public List<PatientDto> getLastLocations(@RequestBody List<Integer> pnoList){
        System.out.println("LocationController.getLastLocations");
        System.out.println("pno 리스트 확인 = " + pnoList);

        return  locationService.getLastLocations(pnoList);
    }

    // [6] 앱 실행 FCM 토큰을 발급받아서 레디스에 저장
    @PostMapping("/savefcmtoken")
    public boolean saveFcmToken(@RequestParam int gno , @RequestParam String fcmToken){
        System.out.println("LocationController.saveFcmToken");
        System.out.println("gno = " + gno + ", fcmToken = " + fcmToken);


        return locationService.saveFcmToken(gno, fcmToken);
    }
}
