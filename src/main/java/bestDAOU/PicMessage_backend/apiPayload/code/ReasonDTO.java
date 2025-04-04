package bestDAOU.PicMessage_backend.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ReasonDTO {
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}