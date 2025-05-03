package bestDAOU.PicMessage_backend.service.impl;

import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.dto.FriendsWithTonesDto;
import bestDAOU.PicMessage_backend.dto.ToneBasicInfoDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Member;
import bestDAOU.PicMessage_backend.entity.Tones;
import bestDAOU.PicMessage_backend.exception.ResourceNotFoundException;
import bestDAOU.PicMessage_backend.mapper.FriendsMapper;
import bestDAOU.PicMessage_backend.repository.FriendsRepository;
import bestDAOU.PicMessage_backend.repository.MemberRepository;
import bestDAOU.PicMessage_backend.repository.TonesRepository;
import bestDAOU.PicMessage_backend.service.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TonesRepository tonesRepository;

    @Override
    public FriendsDto addFriend(FriendsDto friendsDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
        // 새 친구 추가 시 ID를 명시적으로 null로 설정
        friendsDto.setId(null);
        // 수신 DTO 로깅
        System.out.println("친구 추가 중: " + friendsDto.getFriendName() + ", 전화번호: " + friendsDto.getFriendPhone());

        Friends friend = FriendsMapper.mapToFriends(friendsDto, member);
        Friends savedFriend = friendsRepository.save(friend);

        // 저장된 엔티티 로깅
        System.out.println("ID가 " + savedFriend.getId() + "인 친구 저장됨");

        return FriendsMapper.mapToFriendsDto(savedFriend);
    }


    @Override
    public FriendsDto getFriendById(Long friendId) {
        Friends friend = friendsRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found with id: " + friendId));
        return FriendsMapper.mapToFriendsDto(friend);
    }

    @Override
    public List<FriendsDto> getFriendsByMemberId(Long memberId) {
        return friendsRepository.findByMemberId(memberId).stream()
                .map(FriendsMapper::mapToFriendsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendsWithTonesDto> getFriendsByMemberIdWithTones(Long memberId) {
        // 1. 회원의 모든 친구 조회
        List<Friends> friends = friendsRepository.findByMemberId(memberId);

        // 2. 각 친구별로 말투 정보를 조회하여 FriendsWithTonesDto 생성
        List<FriendsWithTonesDto> friendsWithTones = new ArrayList<>();

        for (Friends friend : friends) {
            // 친구 기본 정보를 FriendsDto로 변환
            FriendsDto friendsDto = FriendsMapper.mapToFriendsDto(friend);

            // 친구의 커스텀 말투와 모든 기본 말투 조회
            List<Tones> tones = tonesRepository.findByFriendIdOrIsDefaultTrue(friend.getId());

            // Tones 엔티티를 ToneBasicInfoDto로 변환
            List<ToneBasicInfoDto> tonesInfoList = tones.stream()
                    .map(tone -> new ToneBasicInfoDto(tone.getId(), tone.getName(), tone.isDefault()))
                    .collect(Collectors.toList());

            // FriendsWithTonesDto 생성 및 리스트에 추가
            FriendsWithTonesDto friendWithTones = new FriendsWithTonesDto(friendsDto, tonesInfoList);
            friendsWithTones.add(friendWithTones);
        }

        return friendsWithTones;
    }

    @Override
    public FriendsDto updateFriend(Long friendId, FriendsDto friendsDto) {
        Friends friend = friendsRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found with id: " + friendId));
        // 기존 정보를 업데이트
        friend.setFriendName(friendsDto.getFriendName()); // 친구 이름 업데이트
        friend.setFriendPhone(friendsDto.getFriendPhone()); // 친구 전화번호 업데이트
        friend.setFeatures(friendsDto.getFeatures()); // 태그 업데이트
        friend.setMemos(friendsDto.getMemos()); // 메모 업데이트
        friend.setTones(friendsDto.getTones()); // 어조 리스트 업데이트
        friend.setTones_prompt(friendsDto.getTones_prompt()); // 말투 프롬프트 JSON 업데이트
        friend.setRelationType(friendsDto.getRelationType()); // 관계 유형 업데이트
        friend.setGroupName(friendsDto.getGroupName()); // 그룹명 업데이트

        Friends updatedFriend = friendsRepository.save(friend);
        return FriendsMapper.mapToFriendsDto(updatedFriend);
    }

    @Override
    public void deleteFriend(Long friendId) {
        friendsRepository.deleteById(friendId);
    }
}