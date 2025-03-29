package bestDAOU.PicMessage_backend.dto;

import bestDAOU.PicMessage_backend.entity.RelationType;
import bestDAOU.PicMessage_backend.entity.Tone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendsDto {
    private Long id;
    private String friendName;
    private String friendPhone;
    private String friendEmail;
    private String tags; // 태그 문자열
    private String memos; // 메모 문자열
    private String tones; // 어조
    private Long member_id; // Member와 다대일 관계 설정
    private RelationType relationType; // 관계 유형 (친구, 선배, 부모님 등)
    private String groupName; // 그룹명
}
