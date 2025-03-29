package bestDAOU.PicMessage_backend.mapper;

import bestDAOU.PicMessage_backend.dto.MessageDto;
import bestDAOU.PicMessage_backend.entity.Friends;
import bestDAOU.PicMessage_backend.entity.Member;
import bestDAOU.PicMessage_backend.entity.Message;

public class MessageMapper {
    // Message 엔티티 -> MessageDto로 매핑
    public static MessageDto mapToMessageDto(Message message) {
        return new MessageDto(
                message.getMessageId(),
                message.getMember().getId(), // 발송자 ID
                message.getFriends().getId(), // 수신자 ID
                message.getInput_text(),
                message.getGenerated_text(),
                message.getSend_at(),
                message.getStatus(),
                message.getTone()
        );
    }

    // MessageDto -> Message 엔티티로 매핑
    public static Message mapToMessage(MessageDto messageDto, Member member, Friends friends) {
        return new Message(
                messageDto.getMessageId(),
                member, // Member 객체 설정
                friends, // Friends 객체 설정
                messageDto.getInput_text(),
                messageDto.getGenerated_text(),
                messageDto.getSend_at(),
                messageDto.getStatus(),
                messageDto.getTone()
        );
    }
}
