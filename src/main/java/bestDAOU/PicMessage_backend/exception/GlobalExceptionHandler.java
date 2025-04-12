package bestDAOU.PicMessage_backend.exception;

import bestDAOU.PicMessage_backend.exception.OpenAIException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OpenAIException.class)
    public ResponseEntity<Object> handleOpenAIException(OpenAIException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "메시지 생성에 실패했습니다. 다시 시도해주세요.");
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "서버 오류가 발생했습니다.");
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}