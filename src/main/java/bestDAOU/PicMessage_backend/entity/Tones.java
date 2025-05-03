package bestDAOU.PicMessage_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tones_id")
    private Long id;

    @Column(name = "tones_name", nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instruction;

    @Column(columnDefinition = "TEXT")
    private String examples;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friends friend;
}
