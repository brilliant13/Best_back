package bestDAOU.PicMessage_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String friendName;

    @Column(nullable = false)
    private String friendPhone;

    @Column
    private String friendEmail;

    @Column
    private String tags; // 태그 문자열

    @Column
    private String memos; // 메모 문자열

    @Column
    private String  tones; // 어조 문자열

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // Member와 다대일 관계 설정

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationType relationType; // 관계 유형 (친구, 선배, 부모님 등)

    @Column(nullable = false)
    private String groupName; // 그룹명

}
