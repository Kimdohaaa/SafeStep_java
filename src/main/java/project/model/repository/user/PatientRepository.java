package project.model.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.model.entity.user.PatientEntity;

@Repository
public  interface PatientRepository extends JpaRepository<PatientEntity, Integer>{
}
