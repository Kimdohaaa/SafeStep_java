package project.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import project.model.dto.user.GuardianDto;
import project.model.entity.BaseTime;

@Entity
@Table(name = "guardian")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class GuardianEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int gno;

    private  String gname;
    private String gemail;
    private  String gphone;
    private String grelation;
    private String gid;
    private  String gpwd;


    // Entity -변환-> DTO 메소드
    public GuardianDto toDto(){
        return GuardianDto.builder()
                .gno(this.gno)
                .gname(this.gname)
                .gemail(this.gemail)
                .gphone(this.gphone)
                .gid(this.gid)
                .gpwd(this.gpwd)
                .createAt(this.getCreateAt())
                .updateAt(this.getUpdateAt())
                .build();
    }

}
