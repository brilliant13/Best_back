package bestDAOU.PicMessage_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}