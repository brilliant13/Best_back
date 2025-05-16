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

        // 특정 키워드가 포함된 메시지 필터링 (송금, 봉투, 선물 키워드 제외)
        List<String> filteredMessages = filterMessages(targetMessages);

        // 추출된 메시지를 문자열로 결합
        String targetContent = String.join("\n", filteredMessages);

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

    private List<String> filterMessages(List<String> messages) {
        // 제외할 키워드 목록
        List<String> excludeKeywords = List.of("송금", "봉투", "선물");

        return messages.stream()
                .filter(msg -> excludeKeywords.stream().noneMatch(msg::contains))
                .collect(Collectors.toList());
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
                        "content", "당신은 대화 텍스트를 분석하여 사용자의 고유한 말투, 어휘 선택, 문장 구조, 표현 방식을 정확하게 식별하는 전문가입니다. " +
                                "제공된 텍스트에서 사용자(" + userName + ")의 말투 패턴을 심층적으로 분석하여 상세한 말투 규칙을 만들어야 합니다."
                ),
                Map.of(
                        "role", "user",
                        "content", "다음은 카카오톡 대화에서 추출한 '" + userName + "'의 메시지들입니다. 이 사용자의 말투 규칙을 JSON 형식으로 생성해주세요. " +
                                "송금, 봉투, 선물 관련 내용은 이미 제외되었습니다.\n\n" +
                                "다음 사항을 반드시 고려하여 분석해주세요:\n" +
                                "1. 사용자가 선호하는 문장 끝맺음 패턴(반말/존댓말, 특정 어미 사용 등)\n" +
                                "2. 자주 사용하는 특정 단어나 이모티콘, 문장 부호 패턴\n" +
                                "3. 문장 길이와 복잡성 정도\n" +
                                "4. 반복적으로 나타나는 표현 습관이나 독특한 어휘 선택\n" +
                                "5. 강조나 감정 표현 방식의 특징\n\n" +
                                "다음과 같은 형식으로 정확히 맞춰주세요(JSON 앞뒤에 다른 텍스트 없이):\n" +
                                "{\n" +
                                "  \"label\": \"[이 사용자의 말투를 가장 잘 설명하는 간결한 이름]\",\n" +
                                "  \"instruction\": \"[이 말투를 구사하기 위한 구체적이고 상세한 지침. 문장 구조, 어휘 선택, 특징적인 표현 방식, 문장 끝맺음, 감정 표현 방식 등 다양한 측면을 포함해 최소 5문장 이상으로 상세히 설명]\",\n" +
                                "  \"examples\": [\n" +
                                "    \"[사용자 말투의 특징이 가장 잘 드러나는 예시 문장 1]\",\n" +
                                "    \"[사용자 말투의 특징이 가장 잘 드러나는 예시 문장 2]\",\n" +
                                "    \"[사용자 말투의 특징이 가장 잘 드러나는 예시 문장 3]\",\n" +
                                "    \"[사용자 말투의 특징이 가장 잘 드러나는 예시 문장 4]\",\n" +
                                "    \"[사용자 말투의 특징이 가장 잘 드러나는 예시 문장 5]\"\n" +
                                "  ]\n" +
                                "}\n\n" +
                                "examples는 반드시 실제 대화에서 추출한 문장으로, 말투의 특징이 가장 명확하게 드러나는 것을 최소 4개 이상 포함해주세요.\n" +
                                "instruction은 구체적이고 실용적으로, 이 말투를 모방하려는 사람이 따라할 수 있을 정도로 상세하게 작성해주세요.\n\n" +
                                "분석할 텍스트:\n" + textContent
                )
        ));
        payload.put("temperature", 0.7);
        payload.put("max_tokens", 1000);

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