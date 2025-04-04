package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "말투 정보")
public class ToneRuleDto {

    @Schema(description = "말투 이름", example = "친근한 말투")
    private String label;

    @Schema(description = "말투 프롬프트", example = "상대방이 편안하고 행복하게 느낄 수 있는 따뜻하고 친근한 톤으로 말하세요.")
    private String instruction;

    @Schema(description = "말투 예시 목록")
    private List<String> examples;
}