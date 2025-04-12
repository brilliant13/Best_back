package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.apiPayload.ApiResponse;
import bestDAOU.PicMessage_backend.dto.MessageDto;
import bestDAOU.PicMessage_backend.dto.MessageGenerationRequestDto;
import bestDAOU.PicMessage_backend.dto.MessageGenerationResponseDto;
import bestDAOU.PicMessage_backend.service.MessageService;
import bestDAOU.PicMessage_backend.service.OpenAIMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Messages", description = "메시지 관련 API")
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private OpenAIMessageService openAIMessageService;

    @Operation(summary = "메시지 전송 API", description = "새로운 메시지를 전송하는 API입니다.")
    @PostMapping
    public ApiResponse<MessageDto> sendMessage(@RequestBody MessageDto messageDto) {
        MessageDto savedMessage = messageService.sendMessage(messageDto);
        return ApiResponse.onSuccess(savedMessage, "메시지가 성공적으로 전송되었습니다.");
    }

    @Operation(summary = "메시지 조회 API", description = "메시지 ID로 메시지를 조회하는 API입니다.")
    @GetMapping("{id}")
    public ApiResponse<MessageDto> getMessageById(
            @Parameter(description = "조회할 메시지 ID", required = true) @PathVariable("id") Long messageId) {
        MessageDto messageDto = messageService.getMessageById(messageId);
        return ApiResponse.onSuccess(messageDto);
    }

    @Operation(summary = "회원별 메시지 조회 API", description = "회원 ID로 해당 회원의 모든 메시지를 조회하는 API입니다.")
    @GetMapping("/member/{memberId}")
    public ApiResponse<List<MessageDto>> getMessagesByMemberId(
            @Parameter(description = "회원 ID", required = true) @PathVariable("memberId") Long memberId) {
        List<MessageDto> messages = messageService.getMessagesByMemberId(memberId);
        return ApiResponse.onSuccess(messages);
    }

    @Operation(summary = "친구별 메시지 조회 API", description = "친구 ID로 해당 친구와의 모든 메시지를 조회하는 API입니다.")
    @GetMapping("/friend/{friendId}")
    public ApiResponse<List<MessageDto>> getMessagesByFriendId(
            @Parameter(description = "친구 ID", required = true) @PathVariable("friendId") Long friendId) {
        List<MessageDto> messages = messageService.getMessagesByFriendId(friendId);
        return ApiResponse.onSuccess(messages);
    }

    @Operation(summary = "회원-친구 간 메시지 조회 API", description = "특정 회원과 친구 간의 모든 메시지를 조회하는 API입니다.")
    @GetMapping("/member/{memberId}/friend/{friendId}")
    public ApiResponse<List<MessageDto>> getMessagesByMemberIdAndFriendId(
            @Parameter(description = "회원 ID", required = true) @PathVariable("memberId") Long memberId,
            @Parameter(description = "친구 ID", required = true) @PathVariable("friendId") Long friendId) {
        List<MessageDto> messages = messageService.getMessagesByMemberIdAndFriendsId(memberId, friendId);
        return ApiResponse.onSuccess(messages);
    }

    @Operation(summary = "메시지 삭제 API", description = "메시지를 삭제하는 API입니다.")
    @DeleteMapping("{id}")
    public ApiResponse<String> deleteMessage(
            @Parameter(description = "삭제할 메시지 ID", required = true) @PathVariable("id") Long messageId) {
        messageService.deleteMessage(messageId);
        return ApiResponse.onSuccess("메시지가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "메시지 자동 생성 API", description = "OpenAI API를 사용하여 메시지를 자동으로 생성하는 API입니다.")
    @PostMapping("/generate")
    public ApiResponse<MessageGenerationResponseDto> generateMessage(@RequestBody MessageGenerationRequestDto requestDto) {
        MessageGenerationResponseDto responseDto = openAIMessageService.generateMessage(requestDto);
        return ApiResponse.onSuccess(responseDto, "메시지가 성공적으로 생성되었습니다.");
    }
}