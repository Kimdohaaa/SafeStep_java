package project.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.service.user.GuardianService;

@RestController
@RequestMapping("/guardian")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GuardianController {
    private final GuardianService guardianService;

}
