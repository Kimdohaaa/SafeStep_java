package project.model.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.model.entity.user.GuardianEntity;

@Repository
public interface GuardianRepository extends JpaRepository<GuardianEntity, Integer> {
    // [1] 보호자 정보 조회
    @Query(value = "select * from guardian where gid = :gid", nativeQuery = true)
    GuardianEntity findByGid(String gid);

    // [2] 보호자 정보 수정
    @Modifying
    @Query(value = "update guardian set gname = :gname, gemail = :gemail, gphone = :gphone where gno = :gno", nativeQuery = true)
    int updateGuardian(String gname, String gemail, String gphone, int gno);
}
