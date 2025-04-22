package project.controller.location;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.PatientDto;
import project.service.location.LocationService;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private  final LocationService locationService;

    // [1] 환자 안전 위치 최초 등록
    @PostMapping
    public boolean enrollLocation(@RequestBody PatientDto patientDto){
        System.out.println("LocationController.enrollLocation");
        System.out.println("patientDto = " + patientDto);

        return  locationService.enrollLocation(patientDto);
    }

    // [2] 현재 로그인된 Gno 에 등록된 환자들의 위치 정보 조회
    @GetMapping("/findLocation")
    public List<PatientDto> findLocation(@RequestParam int gno){
        System.out.println("LocationController.findLocation");
        System.out.println("gno = " + gno);

        return locationService.findLocation(gno);
    }
}
