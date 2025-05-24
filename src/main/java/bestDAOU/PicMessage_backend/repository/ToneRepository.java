package bestDAOU.PicMessage_backend.repository;

import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Tones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToneRepository  extends JpaRepository<Tones,Long> {

    Optional<Tones> findFirstByFriendAndIsDefaultTrue(Friends friend);
}
