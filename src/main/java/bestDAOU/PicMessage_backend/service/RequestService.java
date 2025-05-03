package bestDAOU.PicMessage_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Value("${ppurio.api.key}")
    private String PpurioAiApiKey;

    private static final Integer TIME_OUT = 5000;
    private static final String PPURIO_ACCOUNT = "jjww6368";
    private static final String FROM = "01092014486";
    private static final String URI = "https://message.ppurio.com";

    private Map<String, Object> getToken(String baseUri, String BasicAuthorization) {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(baseUri + "/v1/token", "Basic " + BasicAuthorization);
            conn = createConnection(request);
            Map<String, Object> tokenResponse = getResponseBody(conn);
            System.out.println("Token Response: " + tokenResponse);

            if (!tokenResponse.containsKey("token")) {
                throw new RuntimeException("Token not found in response: " + tokenResponse);
            }
            return tokenResponse;
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    private <T> HttpURLConnection createConnection(Request request, T requestObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInputString = objectMapper.writeValueAsString(requestObject);
        HttpURLConnection connect = createConnection(request);
        connect.setDoOutput(true);

        try (OutputStream os = connect.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connect;
    }

    private HttpURLConnection createConnection(Request request) throws IOException {
        URL url = new URL(request.getRequestUri());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", request.getAuthorization());
        conn.setConnectTimeout(TIME_OUT);
        conn.setReadTimeout(TIME_OUT);
        System.out.println("Request URI: " + request.getRequestUri());
        System.out.println("Authorization: " + request.getAuthorization());
        return conn;
    }

    private Map<String, Object> getResponseBody(HttpURLConnection conn) {
        InputStream inputStream;
        try {
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            if (responseCode != 200) {
                String errorResponse = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().collect(Collectors.joining("\n"));
                System.err.println("Error Response: " + errorResponse);
                throw new RuntimeException("API Error: " + errorResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException("API 응답 코드 확인 실패", e);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String inputLine;
            StringBuilder responseBody = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                responseBody.append(inputLine);
            }
            return convertJsonToMap(responseBody.toString());
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }

    private Map<String, Object> convertJsonToMap(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패: " + jsonString, e);
        }
    }

    public List<Map<String, Object>> requestSendWithImage(List<SendMessageRequest> messages) {
        String basicAuthorization = Base64.getEncoder().encodeToString((PPURIO_ACCOUNT + ":" + PpurioAiApiKey).getBytes());
        Map<String, Object> tokenResponse = getToken(URI, basicAuthorization);
        String token = (String) tokenResponse.get("token");

        List<Map<String, Object>> responses = new ArrayList<>();
        for (SendMessageRequest message : messages) {
            try {
                Map<String, Object> sendResponse;
                if (message.getImageBase64() == null || message.getImageBase64().isEmpty()) {
                    // 이미지가 없는 경우 문자만 전송
                    sendResponse = send(URI, token, message.getRecipientPhoneNumber(), message.getMessageContent());
                } else {
                    // 이미지가 있는 경우 MMS 전송
                    sendResponse = sendWithImage(URI, token, message.getRecipientPhoneNumber(), message.getMessageContent(), message.getImageBase64());
                }
                responses.add(sendResponse);
            } catch (RuntimeException e) {
                System.err.println("Error sending message to: " + message.getRecipientPhoneNumber());
                e.printStackTrace();
                throw e;
            }
        }
        return responses;
    }

    private Map<String, Object> send(String baseUri, String accessToken, String recipientPhoneNumber, String messageContent) {
        HttpURLConnection conn = null;
        try {
            String bearerAuthorization = String.format("Bearer %s", accessToken);
            Request httpRequest = new Request(baseUri + "/v1/message", bearerAuthorization);
            conn = createConnection(httpRequest, createSendParams(recipientPhoneNumber, messageContent));
            return getResponseBody(conn);
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // 이미지 없이 전송 LMS
    private Map<String, Object> createSendParams(String recipientPhoneNumber, String messageContent) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", PPURIO_ACCOUNT);
        params.put("messageType", "LMS"); // 이미지 없이 전송이므로 LMS로 설정
        params.put("content", messageContent);
        params.put("from", FROM);
        params.put("targets", List.of(Map.of("to", recipientPhoneNumber, "name", "수신자")));
        params.put("refKey", RandomStringUtils.random(32, true, true));
        params.put("rejectType", "AD");
        params.put("duplicateFlag", "y");
        params.put("subject", "문자 메시지");
        params.put("targetCount", 1); // 대상 수는 1로 설정
        return params;
    }

    private Map<String, Object> sendWithImage(String baseUri, String accessToken, String recipientPhoneNumber, String messageContent, String imageBase64) {
        HttpURLConnection conn = null;
        try {
            String bearerAuthorization = String.format("Bearer %s", accessToken);
            Request httpRequest = new Request(baseUri + "/v1/message", bearerAuthorization);
            conn = createConnection(httpRequest, createSendParamsWithImage(recipientPhoneNumber, messageContent, imageBase64));
            return getResponseBody(conn);
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // 이미지 포함 전송
    private Map<String, Object> createSendParamsWithImage(String recipientPhoneNumber, String messageContent, String imageBase64) {
        int originalSize = (int) Math.ceil((imageBase64.length() * 3) / 4.0);
        if (imageBase64.endsWith("==")) {
            originalSize -= 2;
        } else if (imageBase64.endsWith("=")) {
            originalSize -= 1;
        }

        List<Map<String, Object>> targets = List.of(
                Map.of("to", recipientPhoneNumber, "name", "수신자")
        );

        Map<String, Object> params = new HashMap<>();
        params.put("account", PPURIO_ACCOUNT);
        params.put("messageType", "MMS");
        params.put("content", messageContent);
        params.put("from", FROM);
        params.put("files", List.of(Map.of(
                "size", originalSize,
                "name", "image.jpg",
                "data", imageBase64
        )));
        params.put("targets", targets);
        params.put("targetCount", targets.size());
        params.put("refKey", RandomStringUtils.random(32, true, true));
        params.put("rejectType", "AD");
        params.put("duplicateFlag", "y");
        params.put("subject", "이미지 메시지");
        return params;
    }

    public static class SendMessageRequest {
        private String recipientPhoneNumber;
        private String messageContent;
        private String imageBase64;

        public String getRecipientPhoneNumber() {
            return recipientPhoneNumber;
        }

        public void setRecipientPhoneNumber(String recipientPhoneNumber) {
            this.recipientPhoneNumber = recipientPhoneNumber;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public String getImageBase64() {
            return imageBase64;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }
    }
}

class Request {
    private String requestUri;
    private String authorization;

    public Request(String requestUri, String authorization) {
        this.requestUri = requestUri;
        this.authorization = authorization;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getAuthorization() {
        return authorization;
    }
}
