package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.FriendsDto;
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
    private final FriendsService friendsService;

    @Value("${gpt.api.key}")
    private String gptApiKey;

    private static final Map<String, String> fieldFriendlyNames = Map.of(
            "friendName", "이름을 알려주세요 😊",
            "friendPhone", "전화번호를 알려주세요 📱",
            "friendEmail", "이메일 주소를 알려주세요 ✉️",
            "features", "이 친구의 특징은 어떤가요?",
            "memos", "기억해두고 싶은 내용을 말씀해주세요!",
            "groupName", "이 사람을 어떤 그룹에 추가하고 싶나요? (예 : 대학친구, 군대동기, 선생님)",
            "relationType", "관계 유형을 알려주세요 (예: 친구, 가족 등)"
    );

    public Map<String, Object> handleUserMessage(String userMessage) {
        String gptResponse = callGPT(userMessage);
        Map<String, Object> parsed = parseGptResponse(gptResponse);
        String action = (String) parsed.get("action");

        if ("send_message".equals(action)) {
            Map<String, Object> params = (Map<String, Object>) parsed.get("params");
            Object recipientObj = params.get("recipient");
            String messageContent = (String) params.get("message");

            // ✅ 메시지 내용이 없으면 사용자에게 다시 요청
            if (isBlank(messageContent)) {
                return Map.of(
                        "action", "missing_fields",
                        "missing", List.of("message"),
                        "params", params,
                        "message", "어떤 메시지를 보내고 싶으신가요? ✉️"
                );
            }

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
                    sentNames.add(name);
                } else {
                    notFound.add(name);
                }
            }

            String responseMessage = "";
            if (!sentNames.isEmpty()) {
                responseMessage += String.join(", ", sentNames) + "에게 메시지를 전송했습니다.";
            }
            if (!notFound.isEmpty()) {
                responseMessage += " (" + String.join(", ", notFound) + "은(는) 연락처에 없습니다.)";
            }

            return Map.of("response", responseMessage, "result", allResults);
        }

        else if ("add_friend".equals(action)) {
            Map<String, Object> params = (Map<String, Object>) parsed.get("params");

            List<String> missing = new ArrayList<>();
            if (isBlank(params.get("friendName"))) missing.add("friendName");
            if (isBlank(params.get("friendPhone"))) missing.add("friendPhone");
            if (isBlank(params.get("friendEmail"))) missing.add("friendEmail");
            if (isBlank(params.get("features"))) missing.add("features");
            if (isBlank(params.get("memos"))) missing.add("memos");
            if (isBlank(params.get("groupName"))) missing.add("groupName");
            if (isBlank(params.get("relationType"))) missing.add("relationType");

            if (!missing.isEmpty()) {
                String friendlyMessage = missing.stream()
                        .map(f -> fieldFriendlyNames.getOrDefault(f, f + " 정보를 입력해주세요"))
                        .findFirst()
                        .orElse("필수 정보를 알려주세요!");

                return Map.of(
                        "action", "missing_fields",
                        "missing", missing,
                        "params", params,
                        "message", friendlyMessage
                );
            }

            FriendsDto dto = new FriendsDto();
            dto.setFriendName((String) params.getOrDefault("friendName", ""));
            dto.setFriendPhone((String) params.getOrDefault("friendPhone", ""));
            dto.setFriendEmail((String) params.getOrDefault("friendEmail", ""));
            dto.setFeatures((String) params.getOrDefault("features", ""));
            dto.setMemos((String) params.getOrDefault("memos", ""));
            dto.setGroupName((String) params.getOrDefault("groupName", "기본"));
            dto.setRelationType((String) params.getOrDefault("relationType", ""));
            dto.setSelectedToneId(params.get("selectedToneId") != null
                    ? Long.parseLong(params.get("selectedToneId").toString())
                    : null);
            dto.setId(null);
            dto.setMember_id(1L);

            FriendsDto saved = friendsService.addFriend(dto, 1L);
            return Map.of("response", saved.getFriendName() + "의 연락처가 성공적으로 저장되었습니다.");
        }

        else if ("text_response_greeting".equals(action)) {
            String gptText = (String) parsed.get("response");
            return Map.of("response", gptText); // 깔끔하게 인사말만 보여줌
        }

        else if ("text_response_general".equals(action)) {
            String gptText = (String) parsed.get("response");
            String appended = gptText + "\n\n📌 참고로, 이 서비스는 다음 기능을 지원해요:\n" +
                    "- 💬 친구에게 따뜻한 문자 보내기\n" +
                    "- 🧑‍🤝‍🧑 연락처 자동 등록\n" +
                    "- 🎁 명절/크리스마스/부고 인사 샘플 생성\n\n예: '크리스마스 인사 보내줘', '홍길동에게 문자 보내줘'";
            return Map.of("response", appended);
        }

        return Map.of("response", "알 수 없는 요청 형식입니다.\n\n📌 이 서비스는 문자 생성, 인사말 작성, 지인 등록 기능을 지원합니다. 예: '연락처 추가해줘', '추석 인사 보내줘'");
    }

    private boolean isBlank(Object value) {
        return value == null || value.toString().trim().isEmpty();
    }

    private String callGPT(String userMessage) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "너는 사용자의 자연어 명령을 분석해서 액션을 JSON 형식으로 반환해야 해. \"보내고 싶어\", \"보내줘\", \"메시지\" 등의 문구가 포함되면 무조건 send_message로 해석해야 해.\n" +
                                            "연락처 추가 요청은 '추가해줘', '연락처 등록', '저장해줘' 등의 명령일 때만 add_friend로 해석해.\n\n\n" +

                                            "### 1. 문자 전송 요청\n" +
                                            "- 형식: {\"action\": \"send_message\", \"params\": {\"recipient\": [\"이름1\", \"이름2\"], \"message\": \"보낼 문자 내용\"}}\n" +
                                            "- 예: '홍길동에게 오늘 뭐해? 보내줘'\n\n" +
                                            "### 2. 연락처 추가 요청\n" +
                                            "- 형식: {\"action\": \"add_friend\", \"params\": {\n" +
                                            "  \"friendName\": \"홍길동\",\n" +
                                            "  \"friendPhone\": \"01012345678\",\n" +
                                            "  \"friendEmail\": \"email@example.com\",\n" +
                                            "  \"features\": \"유쾌함\",\n" +
                                            "  \"memos\": \"대학교 친구\",\n" +
                                            "  \"groupName\": \"대학교\",\n" +
                                            "  \"relationType\": \"친구\",\n" +
                                            "  \"selectedToneId\": 1\n}}\n\n" +
                                            "### 3. 인사말 샘플 요청\n" +
                                            "다음 요청이 오면 → 반드시 \"action\": \"text_response_greeting\" 으로 응답하세요:\n" +
                                            "- \"추석 안부 인사 만들어줘\"\n" +
                                            "- \"부고 문자 예시 알려줘\"\n" +
                                            "- \"크리스마스 인사 보내줘\"\n" +
                                            "- \"고객 감사 문자 보내줘\"\n" +
                                            "형식 예:\n" +
                                            "{\"action\": \"text_response_greeting\", \"response\": \"추석을 맞아 따뜻한 마음을 전합니다. 풍성한 한가위 되세요!\"}\n" +
                                            "### 4. 일반 질문 응답\n" +
                                            "서비스와 무관한 일반 질문에는 \"action\": \"text_response_general\" 로 응답하고,\n" +
                                            "\"response\"에는 자연스러운 답변을 포함하세요.\n"),
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
