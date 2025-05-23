package project.model.dto.user;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.user.PatientEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDto {
    private  int pno;

    @Column(nullable = true)
    private  String pname;
    @Column(nullable = true, unique = true)
    private  String  pnumber;
    @Column(nullable = true)
    private  boolean pgender;
    @Column(nullable = true)
    private  String page;
    @Column(nullable = true)
    private  int pgrade;
    private double plat; // 기본위치 위도
    private double plon; // 기본위치 경도
    private  int psafe; // 기본위치 기준 안정 반경
    private boolean pstate; // 기본위치 벗어남 여부
    @Column(nullable = true)
    private String relation;

    private String pphone;

    private  int gno;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // DTO -변환-> Entity 메소드
    public PatientEntity toEntity(){
        return  PatientEntity.builder()
                .pno(this.pno)
                .pname(this.pname)
                .pnumber(this.pnumber)
                .pgender(this.pgender)
                .page(this.page)
                .pgrade(this.pgrade)
                .plat(this.plat)
                .plon(this.plon)
                .psafe(this.psafe)
                .pstate(this.pstate)
                .relation(this.relation)
                .gno(this.gno)
                .pphone(this.pphone)
                .build();
    }

}
