package project.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.dto.user.GuardianDto;
import project.service.user.GuardianService;

@RestController
@RequestMapping("/guardian")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GuardianController {
    private final GuardianService guardianService;

    // [1] 보호자 회원가입
    @PostMapping("/signup")
    public boolean signup(@RequestBody GuardianDto guardianDto){
        System.out.println("GuardianController.saveGuardian");
        System.out.println("guardianDto = " + guardianDto);

        GuardianDto result = guardianService.signup(guardianDto);
        if(result == null){
            return  false;
        }
        return true;
    }

    // [2] 로그인
    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestBody GuardianDto guardianDto){
        System.out.println("GuardianController.login");
        System.out.println("guardianDto = " + guardianDto);

        String token = guardianService.login(guardianDto);

        if (token == null) {
            return null;
        }

        // 세션에 로그인 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginToken", token);

        return token;
    }

    // [3] 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션 가져오기 (없으면 null)
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }

        return ResponseEntity.ok("로그아웃 성공");
    }


    // [4] 로그인 된 보호자 정보 조회
    @GetMapping("/info")
    public GuardianDto findInfo(@RequestHeader("Authorization") String token){
        System.out.println("GuardianController.findInfo");
        System.out.println("token = " + token);

        return guardianService.findInfo(token);

    }

    // [5] 보호자 수정
    @PutMapping("/update")
    public boolean update(@RequestBody GuardianDto guardianDto){
        System.out.println("GuardianController.update");
        System.out.println("guardianDto = " + guardianDto);

        return  guardianService.update(guardianDto);
    }

    // [6] 탈퇴
    @DeleteMapping
    public boolean delete(@RequestParam int gno, @RequestParam String gpwd){
        System.out.println("GuardianController.delete");
        System.out.println("gno = " + gno + ", gpwd = " + gpwd);

        return guardianService.delete(gno, gpwd);
    }
}
