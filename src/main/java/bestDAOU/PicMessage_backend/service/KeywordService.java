package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.KeywordRequestDto;
import bestDAOU.PicMessage_backend.dto.KeywordResponseDto;

public interface KeywordService {
    KeywordResponseDto extractKeywords(KeywordRequestDto requestDto);
}