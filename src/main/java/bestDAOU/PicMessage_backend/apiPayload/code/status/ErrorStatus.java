package bestDAOU.PicMessage_backend.apiPayload.code.status;

import bestDAOU.PicMessage_backend.apiPayload.code.BaseCode;
import bestDAOU.PicMessage_backend.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 일반적인 오류
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "리소스를 찾을 수 없습니다."),

    // 회원 관련 오류
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "존재하지 않는 회원입니다."),
    _MEMBER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "MEMBER409", "이미 존재하는 이메일입니다."),
    _MEMBER_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "MEMBER401", "로그인에 실패했습니다."),

    // 친구 관련 오류
    _FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND404", "존재하지 않는 친구입니다."),
    _FRIEND_DUPLICATED(HttpStatus.CONFLICT, "FRIEND409", "이미 등록된 친구입니다."),

    // 메시지 관련 오류
    _MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE404", "존재하지 않는 메시지입니다."),
    _MESSAGE_SEND_FAILED(HttpStatus.BAD_REQUEST, "MESSAGE400", "메시지 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return new ReasonDTO(this.httpStatus, this.code, this.message);
    }
}