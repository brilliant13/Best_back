package bestDAOU.PicMessage_backend.repository;

import bestDAOU.PicMessage_backend.entity.Tones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TonesRepository extends JpaRepository<Tones, Long> {
    List<Tones> findByFriendId(Long friendId);
    List<Tones> findByIsDefaultTrue();
    List<Tones> findByFriendIdOrIsDefaultTrue(Long friendId);

    // 기본 말투의 개수를 세는 메소드
    long countByIsDefaultTrue();
}