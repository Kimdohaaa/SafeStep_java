package project.model.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.model.entity.user.PatientEntity;

import java.util.List;

@Repository
public  interface PatientRepository extends JpaRepository<PatientEntity, Integer>{
    // [1] 특정 보호자의 환자 전체 조회
    @Query(value = "select * from patient where gno = :gno", nativeQuery = true)
    List<PatientEntity> findByGno(int gno);

    // [2] 환자 정보 수정
    @Modifying
    @Query(value = """
        update patient set pname = :pname , pnumber = :pnumber , pgender = :pgender,
            page = :page, pgrade = :pgrade , relation = :relation, pphone = :pphone where pno = :pno
    """,nativeQuery = true)
    int updatePatient(String pname, String pnumber, boolean pgender,
                      String page, int pgrade, String relation, int pno, String pphone);

    // [4] 환자 안전 위치 등록
    @Modifying
    @Query(value = """
            update patient set plon = :plon, plat = :plat where pno = :pno
            """,nativeQuery = true)
    int enrollLocation(double plon, double plat, int pno);

    // [5] 휴대폰번호로 pno 조회
    @Query(value = "select pno from patient where pphone = :pphone",nativeQuery = true)
    Integer findPno(String pphone);

    // [6] 환자의 안전 위치 조회
    @Query(value = "select * from patient where pno = :pno",nativeQuery = true)
    PatientEntity findSaveLocation(int pno);

    // [7] 특정환자의 보호자 PK 번호 조회
    @Query(value = "select * from patient where pno = :pno", nativeQuery = true)
    PatientEntity findPatientByGno(int pno);
}
