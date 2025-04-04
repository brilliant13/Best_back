package bestDAOU.PicMessage_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ToneAnalyzerService {

    @Value("${gpt.api.key}")
    private String gptApiKey;

    @Value("${gpt.api.url}")
    private String gptApiUrl;

    @Value("${gpt.model}")
    private String gptModel;

    private final RestTemplate restTemplate;

    public ToneAnalyzerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String analyzeTextFile(MultipartFile file, String targetName) throws IOException {
        String fileContent = readFileContent(file);

        // 타겟 사용자 의 메시지만 추출
        List<String> targetMessages = extractMessagesForUser(fileContent, targetName);

        if (targetMessages.isEmpty()) {
            throw new IllegalArgumentException("파일에서 '" + targetName + "'의 메시지를 찾을 수 없습니다.");
        }

        // 추출된 메시지를 문자열로 결합
        String targetContent = String.join("\n", targetMessages);

        return generateToneRuleFromContent(targetContent, targetName);
    }

    private List<String> extractMessagesForUser(String fileContent, String userName) {
        List<String> messages = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[" + Pattern.quote(userName) + "\\]\\s*\\[[^\\]]+\\]\\s*(.+)");

        for (String line : fileContent.split("\n")) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                messages.add(matcher.group(1).trim());
            }
        }

        return messages;
    }

    private String readFileContent(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public String generateToneRuleFromContent(String textContent, String userName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + gptApiKey);

        // GPT API 요청 데이터 구성
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", gptModel);
        payload.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", "당신은 텍스트를 분석하여 특정 사용자의 말투와 대화 패턴을 식별하는 AI 어시스턴트입니다. " +
                                "제공된 텍스트에서 사용자(" + userName + ")의 말투 스타일을 추출하고 특정 JSON 형식으로 규칙을 만드는 것이 당신의 임무입니다."
                ),
                Map.of(
                        "role", "user",
                        "content", "다음은 '" + userName + "'의 메시지입니다. 이 사용자의 말투 규칙을 JSON 형식으로 생성해주세요. 다음과 같은 형식으로 정확히 맞춰주세요(JSON 앞뒤에 다른 텍스트 없이):\n" +
                                "{\n" +
                                "  \"label\": \"[말투 특성을 나타내는 이름 (예: 친근한 말투, 격식있는 말투, 공손한 말투 등)]\",\n" +
                                "  \"instruction\": \"[이 말투로 말하는 방법에 대한 지침]\",\n" +
                                "  \"examples\": [\n" +
                                "    \"[예시 1]\",\n" +
                                "    \"[예시 2]\",\n" +
                                "    \"[예시 3]\"\n" +
                                "    \"[예시 4]\"\n" +
                                "    \"[예시 5]\"\n" +
                                "  ]\n" +
                                "}\n\n" +
                                "분석할 텍스트:\n" + textContent
                )
        ));
        payload.put("temperature", 0.7);
        payload.put("max_tokens", 800);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            // GPT API 호출
            Map<String, Object> response = restTemplate.postForObject(gptApiUrl, entity, Map.class);

            // 응답에서 생성된 텍스트 추출
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String generatedText = (String) message.get("content");

            // JSON 형식의 텍스트만 추출
            return extractJsonFromText(generatedText);

        } catch (Exception e) {
            throw new RuntimeException("GPT API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private String extractJsonFromText(String text) {
        // 텍스트에서 JSON 부분만 추출
        int startIndex = text.indexOf("{");
        int endIndex = text.lastIndexOf("}") + 1;

        if (startIndex >= 0 && endIndex > startIndex) {
            return text.substring(startIndex, endIndex);
        }

        // JSON을 찾지 못한 경우 원본 텍스트 반환
        return text;
    }
}