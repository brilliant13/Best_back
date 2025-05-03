package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "말투 변환 응답 DTO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToneTransformResponseDto {
    @Schema(description = "변환된 텍스트", example = "안녕~! 오늘 날씨 진짜 대박이다!")
    private String transformedText;

    @Schema(description = "사용된 말투 이름", example = "친근한 말투")
    private String toneName;
}