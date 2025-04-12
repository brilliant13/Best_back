package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.service.FriendsService;
import bestDAOU.PicMessage_backend.service.ToneAnalyzerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tone-analyzer")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name = "말투 분석", description = "텍스트 파일에서 특정 사용자의 말투를 분석하여 말투를 추출하는 API")
public class ToneAnalyzerController {

    private final ToneAnalyzerService toneAnalyzerService;
    private final FriendsService friendsService;

    @Autowired
    public ToneAnalyzerController(ToneAnalyzerService toneAnalyzerService, FriendsService friendsService) {
        this.toneAnalyzerService = toneAnalyzerService;
        this.friendsService = friendsService;
    }

    @Operation(summary = "말투 추출 및 저장", description = "텍스트 파일에서 특정 사용자의 말투를 분석하여 말투를 추출하고 해당 친구 정보에 저장합니다.")
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeTone(
            @Parameter(description = "분석할 텍스트 파일", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "분석할 사용자 이름 (예: '임차민')", required = true)
            @RequestParam("userName") String userName,

            @Parameter(description = "저장할 친구 ID", required = true)
            @RequestParam("friendId") Long friendId) {

        try {
            // 서비스를 통해 텍스트 파일에서 특정 사용자의 말투 분석
            String toneRuleJson = toneAnalyzerService.analyzeTextFile(file, userName);

            // JSON 문자열을 실제 객체로 변환 후 다시 직렬화하여 이스케이프 문자 제거
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(toneRuleJson);

            // 응답 객체 생성
            Map<String, Object> response = new HashMap<>();
            response.put("toneRule", jsonNode);

            // 친구 정보 조회
            FriendsDto friendDto = friendsService.getFriendById(friendId);

            // 친구 정보 업데이트
            String label = jsonNode.get("label").asText();
            friendDto.setTones(label); // 말투 레이블 저장
            friendDto.setTones_prompt(toneRuleJson); // 전체 JSON 저장

            // 친구 정보 업데이트
            FriendsDto updatedFriend = friendsService.updateFriend(friendId, friendDto);

            // 응답에 업데이트된 친구 정보 추가
            response.put("updatedFriend", updatedFriend);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "말투 분석 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}