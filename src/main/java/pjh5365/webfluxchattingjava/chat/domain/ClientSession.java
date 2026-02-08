package pjh5365.webfluxchattingjava.chat.domain;

import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * 웹 소켓에 연결한 클라이언트의 세션 정보
 * @author : 박지혁
 * @since : 2026/02/08
 */
@Getter
@RequiredArgsConstructor
public class ClientSession {

    private final String userId; // 사용자 정보
    private final WebSocketSession socketSession; // 사용자의 웹소켓
    private final ObjectMapper objectMapper;
    private final Sinks.Many<ChatMessage> clientSinks = Sinks.many().unicast().onBackpressureBuffer(); // 클라이언트로 전송할 Sinks

    public Flux<ChatMessage> getFlux() { // 사용자의 Sinks 반환
        return clientSinks.asFlux();
    }

    public void sendMessage(ChatMessage chatMessage) {
        clientSinks.tryEmitNext(chatMessage);
    }
}
