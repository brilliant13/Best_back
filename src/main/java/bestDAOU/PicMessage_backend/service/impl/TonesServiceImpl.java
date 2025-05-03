package bestDAOU.PicMessage_backend.service.impl;

import bestDAOU.PicMessage_backend.dto.TonesDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Tones;
import bestDAOU.PicMessage_backend.exception.ResourceNotFoundException;
import bestDAOU.PicMessage_backend.mapper.TonesMapper;
import bestDAOU.PicMessage_backend.repository.FriendsRepository;
import bestDAOU.PicMessage_backend.repository.TonesRepository;
import bestDAOU.PicMessage_backend.service.TonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TonesServiceImpl implements TonesService {

    @Autowired
    private TonesRepository tonesRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Override
    public TonesDto addTones(TonesDto tonesDto, Long friendId) {
        Friends friend = friendsRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found with id: " + friendId));

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
    public TonesDto updateTones(Long tonesId, TonesDto tonesDto) {
        Tones tones = tonesRepository.findById(tonesId)
                .orElseThrow(() -> new ResourceNotFoundException("Tones not found with id: " + tonesId));

        tones.setName(tonesDto.getName());
        tones.setInstruction(tonesDto.getInstruction());
        tones.setExamples(tonesDto.getExamples());

        Tones updatedTones = tonesRepository.save(tones);
        return TonesMapper.mapToTonesDto(updatedTones);
    }

    @Override
    public void deleteTones(Long tonesId) {
        tonesRepository.deleteById(tonesId);
    }
}