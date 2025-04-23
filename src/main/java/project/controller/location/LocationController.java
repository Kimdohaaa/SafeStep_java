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

        return locationService.saveLocation(patientDto);
    }
}
