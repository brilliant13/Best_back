package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.ToneTransformResponseDto;

public interface ToneTransformService {
    ToneTransformResponseDto transformTone(String originalText, Long toneId, Long friendId);
}