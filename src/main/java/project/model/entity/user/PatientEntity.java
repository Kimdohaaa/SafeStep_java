package project.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import project.model.dto.user.PatientDto;
import project.model.entity.BaseTime;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PatientEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int pno;

    private  String pname;
    private  String  pnumber;
    private  boolean pgender;
    private  String page;
    private  int pgrade;
    private double plat; // 기본위치 위도
    private double plon; // 기본위치 경도
    private  int psafe; // 기본위치 기준 안정 반경
    private boolean pstate; // 기본위치 벗어남 여부
    private String relation;

    private  int gno;
    // Entity -변환-> DTO 메소드
    public PatientDto toDto(){
        return PatientDto.builder()
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
                .createAt(this.getCreateAt())
                .updateAt(this.getUpdateAt())
                .gno(this.gno)
                .build();
    }
}
