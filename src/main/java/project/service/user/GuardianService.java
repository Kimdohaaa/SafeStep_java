package project.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.model.repository.user.GuardianRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;

}
