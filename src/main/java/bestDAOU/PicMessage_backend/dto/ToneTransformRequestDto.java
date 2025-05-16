package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "말투 변환 요청 DTO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToneTransformRequestDto {
    @Schema(description = "변환할 원본 텍스트", example = "안녕하세요. 새해 복 많이 받으세요.", required = true)
    private String originalText;

    @Schema(description = "적용할 말투 ID", example = "1", required = true)
    private Long toneId;

    @Schema(description = "친구 ID )", example = "1")
    private Long friendId;
}