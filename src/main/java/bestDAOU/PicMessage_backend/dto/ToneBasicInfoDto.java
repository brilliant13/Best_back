package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "말투 기본 정보 DTO")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ToneBasicInfoDto {

    @Schema(description = "말투 ID", example = "1")
    private Long id;

    @Schema(description = "말투 이름", example = "친근한 말투")
    private String name;

    @Schema(description = "기본 말투 여부", example = "true")
    private boolean isDefault;

    @Schema(description = "말투 예시 목록", example = "[\"안녕! 잘 지냈어?\", \"요즘 어때?\", \"진짜?\"]")
    private List<String> toneExamples;

    public ToneBasicInfoDto(Long id, String name, boolean isDefault, String examples) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;

        // 쉼표로 구분된 문자열을 배열로 변환
        if (examples != null && !examples.isEmpty()) {
            this.toneExamples = java.util.Arrays.asList(examples.split(","));
        } else {
            this.toneExamples = new java.util.ArrayList<>();
        }
    }

}