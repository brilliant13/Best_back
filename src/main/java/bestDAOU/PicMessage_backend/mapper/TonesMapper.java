package bestDAOU.PicMessage_backend.mapper;

import bestDAOU.PicMessage_backend.dto.TonesDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Tones;

public class TonesMapper {

    // Tones 엔티티 -> TonesDto로 매핑
    public static TonesDto mapToTonesDto(Tones tones) {
        return new TonesDto(
                tones.getId(),
                tones.getName(),
                tones.getInstruction(),
                tones.getExamples(),
                tones.getFriend().getId()
        );
    }

    // TonesDto -> Tones 엔티티로 매핑
    public static Tones mapToTones(TonesDto tonesDto, Friends friend) {
        return new Tones(
                tonesDto.getId(),
                tonesDto.getName(),
                tonesDto.getInstruction(),
                tonesDto.getExamples(),
                friend
        );
    }
}