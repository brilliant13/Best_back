package bestDAOU.PicMessage_backend.service.impl;

import bestDAOU.PicMessage_backend.dto.KeywordRequestDto;
import bestDAOU.PicMessage_backend.dto.KeywordResponseDto;
import bestDAOU.PicMessage_backend.service.KeywordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class KeywordServiceImpl implements KeywordService {

    @Value("${gpt.api.key}")
    private String gptApiKey;

    @Value("${gpt.api.url}")
    private String gptApiUrl;

    @Value("${gpt.model}")
    private String gptModel;

    private final RestTemplate restTemplate;

    public KeywordServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public KeywordResponseDto extractKeywords(KeywordRequestDto requestDto) {
        String messageContent = requestDto.getMessageContent();
        if (messageContent == null || messageContent.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }

        List<String> keywords = extractKeywordsFromAPI(messageContent);
        return new KeywordResponseDto(keywords);
    }

    private List<String> extractKeywordsFromAPI(String messageContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + gptApiKey);

        // GPT API 요청 데이터 구성
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", gptModel);
        payload.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", "당신은 텍스트에서 핵심 키워드를 추출하는 AI 어시스턴트입니다. 제공된 문장에서 가장 중요한 핵심 개념이나 대상을 하나의 키워드로 추출해주세요. 이 키워드는 이미지 생성에 사용될 것입니다."
                ),
                Map.of(
                        "role", "user",
                        "content", "다음 문장에서 가장 중요한 핵심 키워드 1개만 추출해주세요. JSON 형식으로 반환해주세요. 형식: {\"keyword\": \"키워드\"}\n\n" + messageContent
                )
        ));
        payload.put("temperature", 0.2);
        payload.put("max_tokens", 100);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            // GPT API 호출
            Map<String, Object> response = restTemplate.postForObject(gptApiUrl, entity, Map.class);

            // 응답에서 생성된 텍스트 추출
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String generatedText = (String) message.get("content");

            // JSON 파싱하여 키워드 리스트 반환
            return parseKeywordFromJson(generatedText);

        } catch (Exception e) {
            throw new RuntimeException("키워드 추출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private List<String> parseKeywordFromJson(String jsonText) {
        List<String> keywords = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"keyword\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher matcher = pattern.matcher(jsonText);

        if (matcher.find()) {
            keywords.add(matcher.group(1));
        }

        // 파싱이 실패했을 경우 다른 패턴으로 시도
        if (keywords.isEmpty()) {
            pattern = Pattern.compile("\"([^\"]+)\"");
            matcher = pattern.matcher(jsonText);

            boolean foundKeyword = false;
            while (matcher.find()) {
                String match = matcher.group(1);
                if (!match.equals("keyword") && !foundKeyword) {
                    keywords.add(match);
                    foundKeyword = true;
                    break;
                }
            }
        }

        return keywords;
    }}