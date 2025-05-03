package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askToGPT(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        System.out.println("userMessage = " + userMessage);
        Map<String, Object> result = chatService.handleUserMessage(userMessage);
        return ResponseEntity.ok(result);
    }
}
