package bestDAOU.PicMessage_backend.dto;

import java.util.List;

public class MessageGenerationRequestDto {
    private String inputText;
    private List<String> keywords;

    // Getters and Setters
    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
