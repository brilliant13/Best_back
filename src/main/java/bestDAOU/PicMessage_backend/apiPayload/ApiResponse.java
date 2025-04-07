package bestDAOU.PicMessage_backend.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result){
        return new ApiResponse<>(true, "200", "요청에 성공하였습니다.", result);
    }

    // 커스텀 메시지와 함께 성공 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result, String message) {
        return new ApiResponse<>(true, "200", message, result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        return new ApiResponse<>(false, code, message, data);
    }

    // 간단한 에러 생성 (데이터 없음)
    public static ApiResponse<?> onFailure(String code, String message){
        return new ApiResponse<>(false, code, message, null);
    }
}