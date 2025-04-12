package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "키워드 추출 요청 DTO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRequestDto {
    @Schema(description = "분석할 메시지 내용", example = "오늘 저녁에 생일 기념으로 레스토랑갈까?", required = true)
    private String messageContent;
}