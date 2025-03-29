package bestDAOU.PicMessage_backend.dto;

import bestDAOU.PicMessage_backend.entity.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long messageId;
    private Long member_id; // 발송자
    private Long friends_id; // 수신자
    private String input_text; // 사용자가 요청한 문자 내용
    private String generated_text; // AI가 생성한 문자 내용
    private LocalDateTime send_at; // 문자 전송 시간
    private MessageStatus status; // 문자 전송 상태
    private String tone; // 사용한 말투
}
