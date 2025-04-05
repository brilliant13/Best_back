package bestDAOU.PicMessage_backend.mapper;

import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Member;

public class FriendsMapper {

    // Friends 엔티티 -> FriendsDto로 매핑
    public static FriendsDto mapToFriendsDto(Friends friends) {
        return new FriendsDto(
                friends.getId(),
                friends.getFriendName(),
                friends.getFriendPhone(),
                friends.getFriendEmail(),
                friends.getFeatures(),
                friends.getMemos(),
                friends.getTones(),
                friends.getTones_prompt(),
                friends.getMember().getId(), // Member의 ID를 직접 설정
                friends.getRelationType(),
                friends.getGroupName()
        );
    }

    // FriendsDto -> Friends 엔티티로 매핑
    public static Friends mapToFriends(FriendsDto friendsDto, Member member) {
        Friends friends = new Friends();
        friends.setId(friendsDto.getId());
        friends.setFriendName(friendsDto.getFriendName());
        friends.setFriendPhone(friendsDto.getFriendPhone());
        friends.setFriendEmail(friendsDto.getFriendEmail());
        friends.setFeatures(friendsDto.getFeatures());
        friends.setMemos(friendsDto.getMemos());
        friends.setTones(friendsDto.getTones());
        friends.setTones_prompt(friendsDto.getTones_prompt());
        friends.setMember(member); // Member 엔티티를 직접 설정
        friends.setRelationType(friendsDto.getRelationType());
        friends.setGroupName(friendsDto.getGroupName());
        return friends;
    }
}
