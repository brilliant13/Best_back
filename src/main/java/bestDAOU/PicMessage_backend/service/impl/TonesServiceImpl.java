package bestDAOU.PicMessage_backend.service.impl;

import bestDAOU.PicMessage_backend.dto.TonesDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Tones;
import bestDAOU.PicMessage_backend.exception.ResourceNotFoundException;
import bestDAOU.PicMessage_backend.mapper.TonesMapper;
import bestDAOU.PicMessage_backend.repository.FriendsRepository;
import bestDAOU.PicMessage_backend.repository.TonesRepository;
import bestDAOU.PicMessage_backend.service.TonesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TonesServiceImpl implements TonesService {

    @Autowired
    private TonesRepository tonesRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Override
    public TonesDto addTones(TonesDto tonesDto, Long friendId) {
        Friends friend = null;

        // 기본 말투가 아닌 경우에만 친구 객체 조회
        if (!tonesDto.isDefault() && friendId != null) {
            friend = friendsRepository.findById(friendId)
                    .orElseThrow(() -> new ResourceNotFoundException("Friend not found with id: " + friendId));
        }

        Tones tones = TonesMapper.mapToTones(tonesDto, friend);
        Tones savedTones = tonesRepository.save(tones);

        return TonesMapper.mapToTonesDto(savedTones);
    }

    @Override
    public TonesDto getTonesById(Long tonesId) {
        Tones tones = tonesRepository.findById(tonesId)
                .orElseThrow(() -> new ResourceNotFoundException("Tones not found with id: " + tonesId));
        return TonesMapper.mapToTonesDto(tones);
    }

    @Override
    public List<TonesDto> getTonesByFriendId(Long friendId) {
        return tonesRepository.findByFriendId(friendId).stream()
                .map(TonesMapper::mapToTonesDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TonesDto> getDefaultTones() {
        return tonesRepository.findByIsDefaultTrue().stream()
                .map(TonesMapper::mapToTonesDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TonesDto> getAllTones(Long friendId) {
        return tonesRepository.findByFriendIdOrIsDefaultTrue(friendId).stream()
                .map(TonesMapper::mapToTonesDto)
                .collect(Collectors.toList());
    }

    @Override
    public TonesDto updateTones(Long tonesId, TonesDto tonesDto) {
        Tones tones = tonesRepository.findById(tonesId)
                .orElseThrow(() -> new ResourceNotFoundException("Tones not found with id: " + tonesId));

        tones.setName(tonesDto.getName());
        tones.setInstruction(tonesDto.getInstruction());
        tones.setExamples(tonesDto.getExamples());
        // isDefault 속성은 업데이트 과정에서 변경되지 않도록 유지

        Tones updatedTones = tonesRepository.save(tones);
        return TonesMapper.mapToTonesDto(updatedTones);
    }

    @Override
    public void deleteTones(Long tonesId) {
        Tones tones = tonesRepository.findById(tonesId)
                .orElseThrow(() -> new ResourceNotFoundException("Tones not found with id: " + tonesId));

        // 기본 말투는 삭제할 수 없도록 제한
        if (tones.isDefault()) {
            throw new IllegalArgumentException("기본 말투는 삭제할 수 없습니다.");
        }

        tonesRepository.deleteById(tonesId);
    }

    @Override
    @Transactional
    public void initializeDefaultTones(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map<String, Object>> tonesList = objectMapper.readValue(
                    jsonData, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> toneData : tonesList) {
                Tones tones = new Tones();
                tones.setName((String) toneData.get("label"));
                tones.setInstruction((String) toneData.get("instruction"));

                // examples 배열을 쉼표로 구분된 문자열로 변환
                StringBuilder examplesBuilder = new StringBuilder();
                List<String> examples = (List<String>) toneData.get("examples");
                for (int i = 0; i < examples.size(); i++) {
                    if (i > 0) {
                        examplesBuilder.append(",");
                    }
                    examplesBuilder.append(examples.get(i));
                }
                tones.setExamples(examplesBuilder.toString());

                // 기본 말투로 설정
                tones.setDefault(true);
                tones.setFriend(null);

                tonesRepository.save(tones);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse tones JSON data", e);
        }
    }
}