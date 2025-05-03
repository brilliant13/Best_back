package bestDAOU.PicMessage_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tones_id")
    private Long id;

    @Column(name = "tones_name", nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instruction;

    @Column(columnDefinition = "TEXT")
    private String examples;

    @Column(nullable = false)
    private boolean isDefault; // 기본 말투 여부 플래그 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private Friends friend; // 기본 말투는 null로 설정
}
