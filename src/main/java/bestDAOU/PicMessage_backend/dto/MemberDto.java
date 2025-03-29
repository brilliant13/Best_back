package bestDAOU.PicMessage_backend.dto;

import bestDAOU.PicMessage_backend.entity.Friends;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
}
