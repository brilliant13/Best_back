package bestDAOU.PicMessage_backend.service.impl;

import bestDAOU.PicMessage_backend.dto.MemberDto;
import bestDAOU.PicMessage_backend.entity.Member;
import bestDAOU.PicMessage_backend.exception.ResourceNotFoundException;
import bestDAOU.PicMessage_backend.mapper.MemberMapper;
import bestDAOU.PicMessage_backend.repository.MemberRepository;
import bestDAOU.PicMessage_backend.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        // 이메일 또는 비밀번호 중복 여부 확인
        boolean isEmailExists = memberRepository.existsByEmail(memberDto.getEmail());

        if (isEmailExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        }

        // CreateMemberDto에서 Member 엔티티로 변환
        Member member = new Member();
        member.setName(memberDto.getName());
        member.setPassword(memberDto.getPassword());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());

        // 회원 저장
        Member savedMember = memberRepository.save(member);

        // 저장된 Member 엔티티를 MemberDto로 변환하여 반환
        return MemberMapper.mapToMemberDto(savedMember);
    }

    @Override
    public MemberDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
        return MemberMapper.mapToMemberDto(member);
    }

    @Override
    public MemberDto updateMember(Long memberId, MemberDto memberDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword());
        Member updatedMember = memberRepository.save(member);
        return MemberMapper.mapToMemberDto(updatedMember);
    }

    @Override
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    @Override
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberMapper::mapToMemberDto)
                .collect(Collectors.toList());
    }
    @Override
    public MemberDto login(String email, String password) {
        List<Member> members = memberRepository.findByEmailAndPassword(email, password);
        if (members.isEmpty()) {
            return null;
        }
        return MemberMapper.mapToMemberDto(members.get(0)); // 첫 번째 일치 항목 반환

    }

}