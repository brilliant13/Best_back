package bestDAOU.PicMessage_backend.dto;

public class MessageGenerationResponseDto {
    private String generatedMessage;

    public MessageGenerationResponseDto(String generatedMessage) {
        this.generatedMessage = generatedMessage;
    }

    // Getters and Setters
    public String getGeneratedMessage() {
        return generatedMessage;
    }

    public void setGeneratedMessage(String generatedMessage) {
        this.generatedMessage = generatedMessage;
    }
}