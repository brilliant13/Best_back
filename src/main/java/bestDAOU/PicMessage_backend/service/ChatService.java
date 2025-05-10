package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.repository.FriendsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final FriendsRepository friendRepository;
    private final RequestService requestService;

    @Value("${gpt.api.key}")
    private String gptApiKey;

    public Map<String, Object> handleUserMessage(String userMessage) {
        String gptResponse = callGPT(userMessage);
        Map<String, Object> parsed = parseGptResponse(gptResponse);
        String action = (String) parsed.get("action");
        if ("send_message".equals(action)) {
            Map<String, Object> params = (Map<String, Object>) parsed.get("params");
            Object recipientObj = params.get("recipient");
            String messageContent = (String) params.get("message");

            List<String> recipients = new ArrayList<>();
            if (recipientObj instanceof String) {
                recipients.add((String) recipientObj);
            } else if (recipientObj instanceof List) {
                recipients = (List<String>) recipientObj;
            }

            List<Map<String, Object>> allResults = new ArrayList<>();
            List<String> notFound = new ArrayList<>();
            List<String> sentNames = new ArrayList<>();
            for (String name : recipients) {
                Optional<Friends> friendOpt = friendRepository.findByFriendName(name);
                if (friendOpt.isPresent()) {
                    String phoneNumber = friendOpt.get().getFriendPhone();
                    RequestService.SendMessageRequest request = new RequestService.SendMessageRequest();
                    request.setRecipientPhoneNumber(phoneNumber);
                    request.setMessageContent(messageContent);

                    allResults.addAll(requestService.requestSendWithImage(List.of(request)));
                    sentNames.add(name); // 보낸 사람 리스트에 추가
                } else {
                    notFound.add(name);
                }
            }

            // 보낸 사람 이름 리스트를 기준으로 응답 메시지 작성
            String responseMessage = "";
            if (!sentNames.isEmpty()) {
                responseMessage += String.join(", ", sentNames) + "에게 메시지를 전송했습니다.";
            }
            if (!notFound.isEmpty()) {
                responseMessage += " (" + String.join(", ", notFound) + "은(는) 연락처에 없습니다.)";
            }


            return Map.of("response", responseMessage, "result", allResults);
        }

        // 문자 기능 외의 일반 질문일 경우: GPT 답변 + 서비스 소개 추가
        if ("text_response".equals(action)) {
            String gptText = (String) parsed.get("response");
            String appended = gptText + "\n\n📌 참고로, 이 서비스는 '문자 자동 생성 및 전송'을 지원합니다. 예: '홍길동에게 안부 문자 보내줘'";
            return Map.of("response", appended);
        }

        return parsed;
    }

    private String callGPT(String userMessage) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "너는 사용자의 자연어 명령을 분석해서 액션을 JSON 형식으로 반환해야 해.\n" +
                                            "예: {\"action\": \"send_message\", \"params\": {\"recipient\": [\"홍길동\", \"김문권\"], \"message\": \"안녕!\"}}\n" +
                                            "또는 일반 대화에는 {\"action\": \"text_response\", \"response\": \"답변내용\"} 형태로 응답해."),
                            Map.of("role", "user", "content", userMessage)
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + gptApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            return root.at("/choices/0/message/content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"action\": \"text_response\", \"response\": \"GPT 호출 중 오류가 발생했습니다.\"}";
        }
    }

    private Map<String, Object> parseGptResponse(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of("action", "text_response", "response", content);
        }
    }
}
