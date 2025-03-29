package bestDAOU.PicMessage_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // 생성일자 자동 설정을 위한 리스너 등록
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 발송자

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private Friends friends; // 수신자

    @Column
    private String input_text; // 사용자가 요청한 문자 내용

    @Column
    private String generated_text; // AI가 생성한 문자 내용

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime send_at; // 문자 전송 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus  status; // 문자 전송 상태

    @Column
    private String tone; // 사용한 말투


}
