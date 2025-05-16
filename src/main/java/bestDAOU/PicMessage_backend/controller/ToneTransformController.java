package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.apiPayload.ApiResponse;
import bestDAOU.PicMessage_backend.dto.ToneTransformRequestDto;
import bestDAOU.PicMessage_backend.dto.ToneTransformResponseDto;
import bestDAOU.PicMessage_backend.service.ToneTransformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tone Transform", description = "텍스트를 특정 말투로 변환하는 API")
@RestController
@RequestMapping("/api/tone-transform")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ToneTransformController {

    @Autowired
    private ToneTransformService toneTransformService;

    @Operation(summary = "말투 변환 API", description = "원본 텍스트를 특정 말투 ID에 맞게 변환하는 API입니다.")
    @PostMapping
    public ApiResponse<ToneTransformResponseDto> transformTone(
            @Parameter(description = "말투 변환 요청 정보", required = true)
            @RequestBody ToneTransformRequestDto requestDto) {
        try {
            ToneTransformResponseDto responseDto = toneTransformService.transformTone(
                    requestDto.getOriginalText(),
                    requestDto.getToneId(),
                    requestDto.getFriendId());
            return ApiResponse.onSuccess(responseDto, "텍스트가 성공적으로 변환되었습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("TONE_TRANSFORM400", e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure("TONE_TRANSFORM500", "텍스트 변환 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }
}