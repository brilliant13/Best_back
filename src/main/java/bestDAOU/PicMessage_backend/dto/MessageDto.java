package bestDAOU.PicMessage_backend.dto;

import bestDAOU.PicMessage_backend.entity.MessageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "메시지 정보 DTO")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    @Schema(description = "메시지 ID", example = "1")
    private Long messageId;

    @Schema(description = "발송자 회원 ID", example = "1", required = true)
    private Long member_id;

    @Schema(description = "수신자 친구 ID", example = "2", required = true)
    private Long friends_id;

    @Schema(description = "사용자가 요청한 원본 문자 내용", example = "안부 인사 보내줘")
    private String input_text;

    @Schema(description = "AI가 생성한 문자 내용", example = "오랜만이에요! 잘 지내고 계신가요? 날씨도 좋은데 주말에 시간 되시면 한번 만나요~")
    private String generated_text;

    @Schema(description = "문자 전송 시간")
    private LocalDateTime send_at;

    @Schema(description = "문자 전송 상태", example = "SENT")
    private MessageStatus status;

    @Schema(description = "사용한 말투", example = "친근한")
    private String tone;
}