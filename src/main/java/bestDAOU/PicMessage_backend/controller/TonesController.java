package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.apiPayload.ApiResponse;
import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.dto.TonesDto;
import bestDAOU.PicMessage_backend.service.FriendsService;
import bestDAOU.PicMessage_backend.service.TonesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Tones", description = "말투 관련 API")
@RestController
@RequestMapping("/api/tones")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TonesController {

    @Autowired
    private TonesService tonesService;

    @Autowired
    private FriendsService friendsService;

    @Operation(summary = "말투 등록 API", description = "특정 친구의 말투를 등록하는 API입니다.")
    @PostMapping("/friend/{friendId}")
    public ApiResponse<TonesDto> addTones(
            @Parameter(description = "말투를 등록할 친구 ID", required = true) @PathVariable Long friendId,
            @RequestBody TonesDto tonesDto) {
        // 새 말투 추가 시 ID를 명시적으로 null로 설정
        tonesDto.setId(null);
        tonesDto.setDefault(false); // 사용자 추가 말투는 기본 말투가 아님을 보장
        tonesDto.setFriend_id(friendId);

        TonesDto savedTones = tonesService.addTones(tonesDto, friendId);
        return ApiResponse.onSuccess(savedTones, "말투가 성공적으로 등록되었습니다.");
    }

    @Operation(summary = "말투 조회 API", description = "말투 ID로 말투 정보를 조회하는 API입니다.")
    @GetMapping("/{id}")
    public ApiResponse<TonesDto> getTonesById(
            @Parameter(description = "조회할 말투 ID", required = true) @PathVariable("id") Long tonesId) {
        TonesDto tonesDto = tonesService.getTonesById(tonesId);
        return ApiResponse.onSuccess(tonesDto);
    }

    @Operation(summary = "친구별 말투 목록 조회 API", description = "친구 ID로 해당 친구의 커스텀 말투를 조회하는 API입니다.")
    @GetMapping("/friend/{friendId}")
    public ResponseEntity<List<TonesDto>> getTonesByFriendId(
            @Parameter(description = "친구 ID", required = true) @PathVariable("friendId") Long friendId) {
        List<TonesDto> tones = tonesService.getTonesByFriendId(friendId);
        return ResponseEntity.ok(tones);
    }

    @Operation(summary = "기본 말투 목록 조회 API", description = "모든 기본 말투를 조회하는 API입니다.")
    @GetMapping("/default")
    public ResponseEntity<List<TonesDto>> getDefaultTones() {
        List<TonesDto> defaultTones = tonesService.getDefaultTones();
        return ResponseEntity.ok(defaultTones);
    }

    @Operation(summary = "모든 말투 조회 API", description = "특정 친구의 커스텀 말투와 모든 기본 말투를 함께 조회하는 API입니다.")
    @GetMapping("/all/{friendId}")
    public ResponseEntity<List<TonesDto>> getAllTones(
            @Parameter(description = "친구 ID", required = true) @PathVariable("friendId") Long friendId) {
        List<TonesDto> allTones = tonesService.getAllTones(friendId);
        return ResponseEntity.ok(allTones);
    }

    @Operation(summary = "말투 정보 수정 API", description = "말투 정보를 수정하는 API입니다.")
    @PatchMapping("/{id}")
    public ApiResponse<TonesDto> updateTones(
            @Parameter(description = "수정할 말투 ID", required = true) @PathVariable Long id,
            @RequestBody TonesDto tonesDto) {
        TonesDto updatedTones = tonesService.updateTones(id, tonesDto);
        return ApiResponse.onSuccess(updatedTones, "말투 정보가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "말투 삭제 API", description = "말투 정보를 삭제하는 API입니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTones(
            @Parameter(description = "삭제할 말투 ID", required = true) @PathVariable("id") Long tonesId) {
        tonesService.deleteTones(tonesId);
        return ApiResponse.onSuccess("말투가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "기본 말투 초기화 API", description = "JSON 파일에서 기본 말투 정보를 초기화하는 API입니다.")
    @PostMapping("/initialize")
    public ApiResponse<?> initializeDefaultTones(@RequestParam("file") MultipartFile file) {
        try {
            String jsonData = new String(file.getBytes());
            tonesService.initializeDefaultTones(jsonData);
            return ApiResponse.onSuccess("기본 말투 정보가 성공적으로 초기화되었습니다.");
        } catch (IOException e) {
            return ApiResponse.onFailure("TONES_ERROR", "기본 말투 초기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "친구별 전체 말투 조회 API", description = "친구 ID로 해당 친구의 커스텀 말투와 모든 기본 말투를 함께 조회하는 API입니다.")
    @GetMapping("/friend/{friendId}/all")
    public ResponseEntity<Map<String, Object>> getAllTonesByFriendId(
            @Parameter(description = "친구 ID", required = true) @PathVariable("friendId") Long friendId) {

        // 친구의 커스텀 말투 조회
        List<TonesDto> customTones = tonesService.getTonesByFriendId(friendId);

        // 기본 말투 조회
        List<TonesDto> defaultTones = tonesService.getDefaultTones();

        // 모든 말투를 하나의 리스트로
        List<TonesDto> allTones = new ArrayList<>();
        allTones.addAll(customTones);
        allTones.addAll(defaultTones);

        // 응답 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("tones", allTones);
        response.put("totalCount", allTones.size());

        return ResponseEntity.ok(response);
    }

}