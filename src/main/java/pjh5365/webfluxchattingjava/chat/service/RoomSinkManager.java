package pjh5365.webfluxchattingjava.chat.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pjh5365.webfluxchattingjava.chat.domain.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * 방별 Sinks를 관리하는 서비스
 * @author : 박지혁
 * @since : 2026/01/16
 */
@Slf4j
@Service
public class RoomSinkManager {

    private final Map<String, Sinks.Many<ChatMessage>> roomSinks = new ConcurrentHashMap<>(); // 각 채팅방별 Sinks를 관리하는 해시맵

    public void emit(String chatroomId, ChatMessage chatMessage) { // 각 채팅방의 Sinks로 메시지를 전송한다.
        Sinks.EmitResult result = getRoomSinks(chatroomId).tryEmitNext(chatMessage); // 메시지 전송
        if (result.isFailure()) { // 메시지 전송에 실패하면 로그를 남긴다.
            log.info("메시지 전송 실패 = {}", chatMessage);
        }
    }

    public Flux<ChatMessage> flux(String chatroomId) { // 채팅방의 Sinks를 Flux로 만들어 반환한다.
        return getRoomSinks(chatroomId).asFlux();
    }

    private Sinks.Many<ChatMessage> getRoomSinks(String chatroomId) { // 각 채팅방의 Sinks를 가져온다. 없다면 새로 만들어 반환한다.
        return roomSinks.computeIfAbsent(chatroomId, id -> Sinks.many().multicast().onBackpressureBuffer()); // 여러 사용자들이 구독하므로 multicast
    }
}
