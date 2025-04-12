package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "키워드 추출 응답 DTO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordResponseDto {
    @Schema(description = "추출된 키워드 목록", example = "[\"생일\", \"레스토랑\"]")
    private List<String> keywords;
}