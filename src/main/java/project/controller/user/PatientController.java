package project.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.PatientDto;
import project.service.user.PatientService;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PatientController {
    private final PatientService patientService;

    // [1] 환자 등록
    @PostMapping("/enroll")
    public int enroll(@RequestBody PatientDto patientDto){
        System.out.println("PatientController.enroll");
        System.out.println("patientDto = " + patientDto);

        return patientService.enroll(patientDto);
    }

    // [2] 특정 보호자의 환자 전체 조회
    @GetMapping("/findall")
    public List<PatientDto> findAll(@RequestParam int gno){
        System.out.println("PatientController.findAll");
        System.out.println("gno = " + gno);

        return patientService.findAll(gno);
    }

    // [3] 환자 개별 조회
    @GetMapping("/find")
    public PatientDto find(@RequestParam int pno) {
        System.out.println("PatientController.find");
        System.out.println("pno = " + pno);

        PatientDto result = patientService.find(pno);
        System.out.println(result);

        return result;
    }

    // [4] 환자 정보 수정
    @PutMapping("/update")
    public int update(@RequestBody PatientDto patientDto){
        System.out.println("PatientController.update");
        System.out.println("patientDto = " + patientDto);

        return patientService.update(patientDto);
    }

    // [5] 환자 정보 삭제
    @DeleteMapping
    public boolean delete(@RequestParam int pno){
        System.out.println("PatientController.delete");
        System.out.println("pno = " + pno);

        return patientService.delete(pno);
    }


}
