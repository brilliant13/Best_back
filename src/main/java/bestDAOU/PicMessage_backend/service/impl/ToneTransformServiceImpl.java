package bestDAOU.PicMessage_backend.service.impl;

import bestDAOU.PicMessage_backend.dto.ToneTransformResponseDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Tones;
import bestDAOU.PicMessage_backend.exception.ResourceNotFoundException;
import bestDAOU.PicMessage_backend.repository.FriendsRepository;
import bestDAOU.PicMessage_backend.repository.TonesRepository;
import bestDAOU.PicMessage_backend.service.ToneTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ToneTransformServiceImpl implements ToneTransformService {

    @Autowired
    private TonesRepository tonesRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Value("${gpt.api.key}")
    private String gptApiKey;

    @Value("${gpt.api.url}")
    private String gptApiUrl;

    @Value("${gpt.model}")
    private String gptModel;

    private final RestTemplate restTemplate;

    public ToneTransformServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ToneTransformResponseDto transformTone(String originalText, Long toneId, Long friendId) {
        if (originalText == null || originalText.trim().isEmpty()) {
            throw new IllegalArgumentException("변환할 텍스트가 비어 있습니다.");
        }

        // 말투 정보 조회
        Tones tone = tonesRepository.findById(toneId)
                .orElseThrow(() -> new ResourceNotFoundException("Tone not found with id: " + toneId));

        // 친구 정보 가져오기
        Optional<Friends> friendOptional = Optional.empty();
        if (friendId != null) {
            friendOptional = friendsRepository.findById(friendId);
        } else if (tone.getFriend() != null) {
            // 말투와 연결된 친구가 있는 경우
            friendOptional = Optional.of(tone.getFriend());
        }

        // GPT API를 이용하여 텍스트 변환
        String transformedText = transformTextWithGPT(originalText, tone, friendOptional);

        // 응답 객체 생성
        return new ToneTransformResponseDto(transformedText, tone.getName());
    }

    private String transformTextWithGPT(String originalText, Tones tone, Optional<Friends> friendOptional) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + gptApiKey);

        // 말투 예시를 배열로 변환
        String[] examplesArray = tone.getExamples().split(",");
        StringBuilder examplesBuilder = new StringBuilder();
        for (String example : examplesArray) {
            examplesBuilder.append("- ").append(example.trim()).append("\n");
        }

        // 친구 정보 추가 (있는 경우)
        StringBuilder friendInfoBuilder = new StringBuilder();
        if (friendOptional.isPresent()) {
            Friends friend = friendOptional.get();
            friendInfoBuilder.append("친구 이름: ").append(friend.getFriendName()).append("\n");

            if (friend.getFeatures() != null && !friend.getFeatures().trim().isEmpty()) {
                friendInfoBuilder.append("친구의 특징: ").append(friend.getFeatures()).append("\n");
            }

            if (friend.getMemos() != null && !friend.getMemos().trim().isEmpty()) {
                friendInfoBuilder.append("친구와의 추억/메모: ").append(friend.getMemos()).append("\n");
            }
        }

        String friendInfo = friendInfoBuilder.toString();
        String promptContent = "다음 텍스트를 '" + tone.getName() + "' 말투로 변환해주세요.\n\n" +
                "말투 지침: " + tone.getInstruction() + "\n\n" +
                "말투 예시:\n" + examplesBuilder.toString() + "\n";

        // 친구 정보가 있으면 추가
        if (!friendInfo.isEmpty()) {
            promptContent += "\n친구 정보:\n" + friendInfo +
                    "\n위 친구 정보를 고려하여 친구와의 관계와 특징, 추억을 자연스럽게 활용해 말투를 변환해주세요.\n";
        }

        promptContent += "\n원본 텍스트: " + originalText;

        // GPT API 요청 데이터 구성
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", gptModel);
        payload.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", "당신은 텍스트의 말투를 변환하는 AI 어시스턴트입니다. " +
                                "주어진 지침과 예시에 따라 원본 텍스트의 내용은 유지하면서 말투를 변경하세요. " +
                                "친구의 특징과 과거 추억을 자연스럽게 반영하여 개인화된 메시지로 만들어주세요. " +
                                "변환된 텍스트만 응답하고 다른 설명은 포함하지 마세요." +
                                "현재 년도는 2025 입니다." +
                                "한글로 대답하세요"
                ),
                Map.of(
                        "role", "user",
                        "content", promptContent
                )
        ));
        payload.put("temperature", 0.7);
        payload.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            // GPT API 호출
            Map<String, Object> response = restTemplate.postForObject(gptApiUrl, entity, Map.class);

            // 응답에서 생성된 텍스트 추출
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            throw new RuntimeException("텍스트 변환 중 오류 발생: " + e.getMessage(), e);
        }
    }
}