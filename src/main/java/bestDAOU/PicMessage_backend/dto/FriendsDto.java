package bestDAOU.PicMessage_backend.dto;

import bestDAOU.PicMessage_backend.entity.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "친구 정보 DTO")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendsDto {

    @Schema(description = "친구 ID", example = "1")
    private Long id;

    @Schema(description = "친구 이름", example = "홍길동", required = true)
    private String friendName;

    @Schema(description = "친구 전화번호", example = "01012345678", required = true)
    private String friendPhone;

    @Schema(description = "친구 이메일", example = "friend@example.com")
    private String friendEmail;

    @Schema(description = "친구 특징 태그 (쉼표로 구분)", example = "친절함,유머러스,활발함")
    private String features;

    @Schema(description = "메모 내용", example = "매주 토요일 만남")
    private String memos;

    @Schema(description = "선호하는 말투 (쉼표로 구분)", example = "친근한,격식있는")
    private String tones;


    @Schema(description = "말투 프롬프트 JSON", example = "{\"label\":\"친근한 말투\",\"instruction\":\"상대방과 친밀한 관계를 나타내는 말투로 말하세요.\",\"examples\":[\"안녕, 잘 지냈어?\"]}")
    private String tones_prompt;

    @Schema(description = "회원 ID (소유자)", example = "1", required = true)
    private Long member_id;

    @Schema(description = "관계 유형", example = "FRIEND", required = true)
    private RelationType relationType;

    @Schema(description = "그룹명", example = "대학친구")
    private String groupName;
}