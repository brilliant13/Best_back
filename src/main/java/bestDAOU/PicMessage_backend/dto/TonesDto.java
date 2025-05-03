package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "말투 정보 DTO")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TonesDto {

    @Schema(description = "말투 ID", example = "1")
    private Long id;

    @Schema(description = "말투 이름", example = "친근한 말투", required = true)
    private String name;

    @Schema(description = "말투 지침", example = "상대방이 편안하고 행복하게 느낄 수 있는 따뜻하고 친근한 톤으로 말하세요.")
    private String instruction;

    @Schema(description = "말투 예시", example = "안녕! 잘 지냈어?, 요즘 어때?, 진짜?")
    private String examples;

    @Schema(description = "친구 ID", example = "1", required = true)
    private Long friend_id;
}