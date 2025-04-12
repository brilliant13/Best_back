package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.apiPayload.ApiResponse;
import bestDAOU.PicMessage_backend.dto.KeywordRequestDto;
import bestDAOU.PicMessage_backend.dto.KeywordResponseDto;
import bestDAOU.PicMessage_backend.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keywords")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name = "키워드 추출", description = "문자 내용에서 핵심 키워드를 추출하는 API")
public class KeywordController {

    private final KeywordService keywordService;

    @Autowired
    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @Operation(summary = "키워드 추출 API", description = "문자 내용에서 핵심 키워드 1개 추출하는 API입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "키워드 추출 성공",
                    content = @Content(schema = @Schema(implementation = KeywordResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    @PostMapping("/extract")
    public ApiResponse<KeywordResponseDto> extractKeywords(@RequestBody KeywordRequestDto requestDto) {
        try {
            KeywordResponseDto responseDto = keywordService.extractKeywords(requestDto);
            return ApiResponse.onSuccess(responseDto, "키워드가 성공적으로 추출되었습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("KEYWORD400", e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure("KEYWORD500", "키워드 추출 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }
}