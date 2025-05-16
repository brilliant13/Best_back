package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.MessageGenerationRequestDto;
import bestDAOU.PicMessage_backend.exception.OpenAIException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class OpenAIService {

    @Value("${gpt.api.key}")
    private String apiKey;

    @Value("${gpt.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${gpt.model:gpt-4}")
    private String model;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public String generateMessage(MessageGenerationRequestDto requestDto) {
        try {
            // 프롬프트 구성
            String prompt = String.format(
                    "다음의 키워드와 내용을 바탕으로 적절한 메시지를 생성해 주세요. 키워드: %s 내용: %s 생성된 메시지: ",
                    String.join(", ", requestDto.getKeywords().isEmpty() ? List.of("없음") : requestDto.getKeywords()),
                    requestDto.getInputText()
            );

            // JSON 요청 본문 생성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            JSONArray messages = new JSONArray();

            // 시스템 메시지 추가
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "당신은 메시지 작성 전문가입니다. 요청된 키워드와 내용을 기반으로 명확하고 적절한 메시지를 생성합니다. "
                    + "메시지는 사용자가 원하는 목적에 맞게 공식적이거나 비공식적인 톤을 반영해야 합니다. "
                    + "중요: 메시지 끝에 발신자 이름이나 '~드림', '~올림' 등의 문구를 절대 추가하지 마세요. "
                    + "발신자 정보(예: '홍길동 드림', '김철수 올림', '감사합니다 [이름] 드림' 등)는 완전히 제외하고 "
                    + "메시지 본문 내용만 생성해주세요. 이름이나 서명이 전혀 없는 순수한 메시지 내용만 반환해야 합니다.");
            messages.put(systemMessage);

            // 사용자 메시지 추가
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);

            requestBody.put("messages", messages);

            // HTTP 요청 생성
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, requestBody.toString());

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            // HTTP 요청 실행
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new OpenAIException("OpenAI API 요청 실패: " + response.code() + " " + response.body().string());
                }

                // 응답 처리
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                String generatedMessage = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim();

                // 발신자 정보 제거 (추가 방어책)
                generatedMessage = removeSignatureIfPresent(generatedMessage);

                return generatedMessage;
            }
        } catch (IOException e) {
            throw new OpenAIException("OpenAI API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }


     // 메시지에서 발신자 서명 부분을 제거
    private String removeSignatureIfPresent(String text) {
        // "~감사합니다", "~드림", "~올림" 등으로 끝나는 패턴 제거
        String[] signaturePatterns = {
                "감사합니다 .+드림$",
                "감사합니다\\s+.+드림$",
                "감사합니다\\s+.+$",
                ".+드림$",
                ".+올림$",
                "^.+드림\\s*$",
                "\\s+드림$",
                "\\s+올림$",
                "[\\w가-힣]+\\s+드림\\.$",
                "[\\w가-힣]+\\s+올림\\.$",
                "\\[당신의 이름\\]\\s+드림\\.$",
                "\\[.+\\]\\s+드림\\.$",
                "\\s*-\\s*[\\w가-힣]+$"  // "- 홍길동" 형식도 제거
        };

        String result = text;
        for (String pattern : signaturePatterns) {
            result = result.replaceAll(pattern, "");
        }

        // 불필요한 공백 제거
        result = result.trim();

        return result;
    }
}