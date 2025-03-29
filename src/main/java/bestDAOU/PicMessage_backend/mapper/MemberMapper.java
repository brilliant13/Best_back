package bestDAOU.PicMessage_backend.mapper;

import bestDAOU.PicMessage_backend.dto.MemberDto;
import bestDAOU.PicMessage_backend.entity.Member;
import bestDAOU.PicMessage_backend.entity.Friends;

import java.util.stream.Collectors;

public class MemberMapper {

    // Member 엔티티 -> MemberDto로 매핑
    public static MemberDto mapToMemberDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getName(),
                member.getPassword(),
                member.getEmail(),
                member.getPhone()
        );
    }

    // MemberDto -> Member 엔티티로 매핑
    public static Member mapToMember(MemberDto memberDto) {
        Member member = new Member(
                memberDto.getId(),
                memberDto.getName(),
                memberDto.getPassword(),
                memberDto.getEmail(),
                memberDto.getPhone()
        );

        return member;
    }
}
