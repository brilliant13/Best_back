package bestDAOU.PicMessage_backend.service;

import bestDAOU.PicMessage_backend.dto.TonesDto;

import java.util.List;

public interface TonesService {
    TonesDto addTones(TonesDto tonesDto, Long friendId);
    TonesDto getTonesById(Long tonesId);
    List<TonesDto> getTonesByFriendId(Long friendId);
    TonesDto updateTones(Long tonesId, TonesDto tonesDto);
    void deleteTones(Long tonesId);
}