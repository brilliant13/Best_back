package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.service.RequestService;
import bestDAOU.PicMessage_backend.service.RequestService.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ppurio")
public class PpurioController {

    @Autowired
    private final RequestService requestService;

    // 메시지와 이미지를 함께 처리하는 요청
    @PostMapping("/send")
    public List<Map<String, Object>> sendMessagesWithImage(@RequestBody List<SendMessageRequest> messages) {
        return requestService.requestSendWithImage(messages);
    }

}
