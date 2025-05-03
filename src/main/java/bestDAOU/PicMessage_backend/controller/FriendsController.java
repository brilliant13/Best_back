package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.apiPayload.ApiResponse;
import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.dto.FriendsWithTonesDto;
import bestDAOU.PicMessage_backend.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friends", description = "친구 관련 API")
@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    @Operation(summary = "친구 등록 API", description = "특정 회원의 친구를 등록하는 API입니다.")
    @PostMapping("/member/{memberId}")
    public ApiResponse<FriendsDto> addFriend(
            @Parameter(description = "친구를 등록할 회원 ID", required = true) @PathVariable Long memberId,
            @RequestBody FriendsDto friendsDto) {
        // 새 친구 추가 시 ID를 명시적으로 null로 설정
        friendsDto.setId(null);
        FriendsDto savedFriend = friendsService.addFriend(friendsDto, memberId);
        return ApiResponse.onSuccess(savedFriend, "친구가 성공적으로 등록되었습니다.");
    }

    @Operation(summary = "친구 조회 API", description = "친구 ID로 친구 정보를 조회하는 API입니다.")
    @GetMapping("{id}")
    public ApiResponse<FriendsDto> getFriendById(
            @Parameter(description = "조회할 친구 ID", required = true) @PathVariable("id") Long friendId) {
        FriendsDto friendDto = friendsService.getFriendById(friendId);
        return ApiResponse.onSuccess(friendDto);
    }

    @Operation(summary = "회원별 친구 목록 조회 API", description = "회원 ID로 해당 회원의 모든 친구를 조회하고 각 친구의 말투 정보를 함께 반환하는 API입니다.")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<FriendsWithTonesDto>> getFriendsByMemberId(
            @Parameter(description = "회원 ID", required = true) @PathVariable("memberId") Long memberId) {
        List<FriendsWithTonesDto> friends = friendsService.getFriendsByMemberIdWithTones(memberId);
        return ResponseEntity.ok(friends);
    }

    @Operation(summary = "친구 정보 수정 API", description = "친구 정보를 수정하는 API입니다.")
    @PatchMapping("{id}")
    public ApiResponse<FriendsDto> updateFriend(
            @Parameter(description = "수정할 친구 ID", required = true) @PathVariable Long id,
            @RequestBody FriendsDto friendsDto) {
        FriendsDto updatedFriend = friendsService.updateFriend(id, friendsDto);
        return ApiResponse.onSuccess(updatedFriend, "친구 정보가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "친구 삭제 API", description = "친구 정보를 삭제하는 API입니다.")
    @DeleteMapping("{id}")
    public ApiResponse<String> deleteFriend(
            @Parameter(description = "삭제할 친구 ID", required = true) @PathVariable("id") Long friendId) {
        friendsService.deleteFriend(friendId);
        return ApiResponse.onSuccess("친구가 성공적으로 삭제되었습니다.");
    }
}