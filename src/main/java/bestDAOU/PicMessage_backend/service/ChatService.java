package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Tones;
import bestDAOU.PicMessage_backend.repository.FriendsRepository;
import bestDAOU.PicMessage_backend.repository.ToneRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final FriendsRepository friendRepository;
    private final ToneRepository toneRepository;
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

        log.debug("handleUserMessage() called with >>> {}", userMessage);

        if (userMessage.contains("맞춤화")) {
            System.out.println("userMessage111 = " + userMessage);
            return handlePersonalizedMessage(userMessage);
        }
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
            // selectedToneId가 없으면 13으로 디폴트
            Long toneId = 13L;
            dto.setSelectedToneId(toneId);
            dto.setId(null);
            dto.setMember_id(1L);

            FriendsDto saved = friendsService.addFriend(dto, 1L);
            return Map.of("response", saved.getFriendName() + "의 연락처가 성공적으로 저장되었습니다.");
        }

        else if ("text_response_greeting".equals(action)) {
            String gptText = (String) parsed.get("response");
            System.out.println("gptText = " + gptText);
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
                                    "너는 사용자의 자연어 명령을 확인해서 다음 규칙에 따라 JSON 액션을 반환해야 해:\n" +
                                            "1) 메시지 전송 요청: “보내고 싶어”, “보내줘”, “메시지” 등의 단어가 있으면 send_message\n" +
                                            "2) 연락처 추가 요청: 문장에 “연락처” 또는 “주소록” 또는 “친구” 같은 단어와 “추가”, “등록”, “저장”, “넣어” 등이 조합되어 있으면 무조건 add_friend\n" +
                                            "   예) “새 연락처를 등록하고 싶어”, “주소록에 추가해줘”, “친구를 추가해주세요” → add_friend\n\n" +
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

    /** “홍길동에게 XX 보내줘” 또는 “홍길동한테 XX 보내줘”에서 “홍길동”만 뽑기 */
    private String extractName(String text) {
        for (String token : text.trim().split("\\s+")) {
            if (token.matches(".+(?:에게|한테)$")) {
                return token.replaceAll("(?:에게|한테)$", "");
            }
        }
        return null;
    }

    private Map<String, Object> handlePersonalizedMessage(String userMessage) {
        log.debug("=== Personalized Flow Start ===");
        log.debug("Raw userMessage: {}", userMessage);

        // 1) 수신자 이름·본문 파싱
        String name = extractName(userMessage);
        String body = userMessage;

        // 2) 동명이인 처리: 이름으로 모두 조회
        List<Friends> sameNameList = friendRepository.findAllByFriendName(name);
        if (sameNameList.isEmpty()) {
            return Map.of("response", name + "님이 주소록에 없습니다. 먼저 연락처를 등록해주세요.");
        }
        if (sameNameList.size() > 1) {
            return Map.of("response", name + "님 이름으로 동명이인이 " + sameNameList.size() + "명 등록되어 있습니다. 구체적인 구분 정보를 입력해주세요.");
        }

        // 이제 정확히 한 명만 선택됨
        Friends friend = sameNameList.get(0);
        // 3) 톤 조회 (선택된 톤이 없으면 기본 톤)
        Tones tone = Optional.ofNullable(friend.getSelectedToneId())
                .flatMap(toneRepository::findById)
                .orElseGet(() ->
                        // 기본 말투가 있으면 꺼내고, 없으면 예외 처리하거나 디폴트 메시지 사용
                        toneRepository.findFirstByFriendAndIsDefaultTrue(friend)
                                .orElseThrow(() ->
                                        new IllegalStateException("기본 말투가 설정되지 않았습니다: " + friend.getFriendName())
                                )
                );

        // 4) GPT 프롬프트 조립
        String prompt = String.format("""
        너는 1:1 대화 형식의 친구 대리 쳇봇이야.
        사용자 요청을 더 자연스럽고 따뜻하게 바꿔줄 때,\s
        **반드시** 수신자의 [특징]과 [메모]를 참고하여 문장 곳곳에 녹여내야 해.

        [수신자 정보]
        이름: %s
        특징: %s
        메모: %s

        [말투 설정]
        말투 이름: %s
        지침: %s
        예시: %s

        [사용자 요청 원문]
        "%s"
        """,
                friend.getFriendName(),
                friend.getFeatures(),
                friend.getMemos(),
                tone.getName(),
                tone.getInstruction(),
                tone.getExamples(),
                body
        );

        System.out.println("prompt = " + prompt);
        // 5) GPT 호출
        String rawResponse = callPersonalizedGPT(prompt);
        log.debug("Raw GPT response: {}", rawResponse);
        // 6) 'content: "..."' 프리픽스 또는 JSON 래퍼 제거 후 본문만 추출
        String aiMessage = rawResponse.trim();
        // 6-1) content: "..." 또는 내용: "..." 패턴으로 감싸인 경우 본문만 추출
        Matcher m = Pattern.compile("^(?:content|내용):\\s*\"([\\s\\S]*)\"$").matcher(aiMessage);
        if (m.find()) {
            aiMessage = m.group(1);
        } else {
            // 6-2) JSON 객체 형태일 경우 content 필드만 꺼내기
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(aiMessage);
                if (node.has("content")) {
                    aiMessage = node.get("content").asText();
                }
            } catch (Exception ignored) {}
        }

        log.debug("Final personalized message: {}", aiMessage);
        // 8) 실제 전송
        RequestService.SendMessageRequest req = new RequestService.SendMessageRequest();
        req.setRecipientPhoneNumber(friend.getFriendPhone());
        req.setMessageContent(aiMessage);
        List<Map<String, Object>> result = requestService.requestSendWithImage(List.of(req));

        // 9) 결과 반환
        String confirmation = String.format(
                "보낸 메시지 : %s\n메시지 전송이 완료되었습니다.",
                aiMessage
        );
        return Map.of(
                "response", confirmation,
                "result",   result
        );
    }
    /** GPT 에 단일 프롬프트를 던져 텍스트를 받아오는 헬퍼 (예시) */
    private String callPersonalizedGPT(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> body = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "너는 친구에게 보내는 따뜻한 1:1 대화 메시지를 다듬어 주는 AI야. " +
                                            "오직 content 필드에만 최종 메시지를 담아 응답해줘."
                            ),
                            Map.of("role", "user", "content", prompt)
                    )
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + gptApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(resp.body());
            return root.at("/choices/0/message/content").asText();
        } catch (Exception e) {
            throw new RuntimeException("맞춤화 GPT 호출 실패", e);
        }
    }
}
