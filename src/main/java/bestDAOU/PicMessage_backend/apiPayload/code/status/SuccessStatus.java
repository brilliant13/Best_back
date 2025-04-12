package bestDAOU.PicMessage_backend.apiPayload.code.status;

import bestDAOU.PicMessage_backend.apiPayload.code.BaseCode;
import bestDAOU.PicMessage_backend.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
//issue test

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "요청에 성공하였습니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "리소스가 생성되었습니다."),

    // 회원 관련 응답
    _MEMBER_CREATED(HttpStatus.CREATED, "MEMBER201", "회원 생성에 성공하였습니다."),
    _MEMBER_READ(HttpStatus.OK, "MEMBER200", "회원 정보 조회에 성공하였습니다."),
    _MEMBER_UPDATED(HttpStatus.OK, "MEMBER201", "회원 정보 수정에 성공하였습니다."),
    _MEMBER_DELETED(HttpStatus.OK, "MEMBER202", "회원 삭제에 성공하였습니다."),

    // 친구 관련 응답
    _FRIEND_CREATED(HttpStatus.CREATED, "FRIEND201", "친구 추가에 성공하였습니다."),
    _FRIEND_READ(HttpStatus.OK, "FRIEND200", "친구 정보 조회에 성공하였습니다."),
    _FRIEND_UPDATED(HttpStatus.OK, "FRIEND201", "친구 정보 수정에 성공하였습니다."),
    _FRIEND_DELETED(HttpStatus.OK, "FRIEND202", "친구 삭제에 성공하였습니다."),

    // 메시지 관련 응답
    _MESSAGE_SENT(HttpStatus.CREATED, "MESSAGE201", "메시지 전송에 성공하였습니다."),
    _MESSAGE_READ(HttpStatus.OK, "MESSAGE200", "메시지 조회에 성공하였습니다."),
    _MESSAGE_DELETED(HttpStatus.OK, "MESSAGE202", "메시지 삭제에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return new ReasonDTO(this.httpStatus, this.code, this.message);
    }
}