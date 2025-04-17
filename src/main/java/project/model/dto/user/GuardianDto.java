package project.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.entity.user.GuardianEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianDto {

    private  int gno;

    private  String gname;
    private String gemail;
    private  String gphone;
    private String grelation;
    private String gid;
    private  String gpwd;

    private LocalDateTime createAt;
    private  LocalDateTime updateAt;

    // DTO -변환-> Entity 메소드
    public GuardianEntity toEntity (){
        return GuardianEntity.builder()
                .gname(this.gname)
                .gemail(this.gemail)
                .gphone(this.gphone)
                .grelation(this.grelation)
                .gid(this.gid)
                .gpwd(this.gpwd)
                .build();
    }

}
