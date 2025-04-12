package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.MessageGenerationRequestDto;
import bestDAOU.PicMessage_backend.dto.MessageGenerationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAIMessageService {

    private final OpenAIService openAIService;

    @Autowired
    public OpenAIMessageService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    public MessageGenerationResponseDto generateMessage(MessageGenerationRequestDto requestDto) {
        String generatedMessage = openAIService.generateMessage(requestDto);
        return new MessageGenerationResponseDto(generatedMessage);
    }
}