package project.model.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.model.entity.user.GuardianEntity;

@Repository
public interface GuardianRepository extends JpaRepository<GuardianEntity, Integer> {
}
