package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "회원 정보 DTO")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이름", example = "김찰스", required = true)
    private String name;

    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;

    @Schema(description = "이메일", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "전화번호", example = "01012341234", required = true)
    private String phone;
}