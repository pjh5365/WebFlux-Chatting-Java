package pjh5365.webfluxchattingjava.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 메시지
 * @author : 박지혁
 * @since : 2026/01/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String userId; // 사용자 정보 (전송자)
    private String chatroomId; // 채팅방 정보
    private String messageId; // 채팅메시지 일련번호
    private String content; // 채팅메시지 내용
}

