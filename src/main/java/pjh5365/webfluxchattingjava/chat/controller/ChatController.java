package pjh5365.webfluxchattingjava.chat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pjh5365.webfluxchattingjava.chat.domain.ChatMessage;
import pjh5365.webfluxchattingjava.chat.service.RoomSinkManager;
import reactor.core.publisher.Mono;

/**
 * 다른 서버로부터 응답을 받을 컨트롤러
 * @author : 박지혁
 * @since : 2026/01/18
 */
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RoomSinkManager roomSinkManager;

    @PostMapping("/chat/send")
    public Mono<Void> getMessage(@RequestBody ChatMessage chatMessage) {
        roomSinkManager.emit(chatMessage.getChatroomId(), chatMessage); // 전달받은 메시지 로컬에서 처리

        return Mono.empty();
    }
}
