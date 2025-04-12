package bestDAOU.PicMessage_backend.repository;

import bestDAOU.PicMessage_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByEmailAndPassword(String email, String password);

    // 중복 체크용 메서드
    boolean existsByEmail(String email);
    boolean existsByPassword(String password);
}
