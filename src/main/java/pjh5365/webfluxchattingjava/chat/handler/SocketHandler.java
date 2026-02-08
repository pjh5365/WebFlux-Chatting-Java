package pjh5365.webfluxchattingjava.chat.handler;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import pjh5365.webfluxchattingjava.chat.domain.ChatMessage;
import pjh5365.webfluxchattingjava.chat.domain.ClientSession;
import pjh5365.webfluxchattingjava.chat.service.HttpPublisher;
import pjh5365.webfluxchattingjava.chat.service.RoomManager;
import reactor.core.publisher.Mono;

/**
 * 채팅 소켓 핸들러
 * @author : 박지혁
 * @since : 2026/01/16
 */
@Component
@RequiredArgsConstructor
public class SocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final HttpPublisher httpPublisher;
    private final RoomManager roomManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String userId = session.getHandshakeInfo()
                .getUri()
                .getQuery()
                .replace("userId=", "");

        List<String> rooms = List.of("room1", "room2"); // 사용자가 실제로 접속하고 있는 채팅방의 일련번호 리스트
        ClientSession clientSession = new ClientSession(userId, session, objectMapper); // 사용자ID, 웹소켓으로 사용자 객체 생성

        roomManager.subscribe(rooms, clientSession); // 사용자가 참여중인 모든 채팅방에 사용자 웹소켓세션 저장

        // 서버 -> 클라이언트로의 전송
        Mono<Void> outbound = session.send(clientSession.getFlux().map(this::toJson).map(session::textMessage));

        // 클라이언트 -> 서버로의 전송
        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::fromJson)
                .doOnNext(msg -> {
                    msg.setMessageId(UUID.randomUUID().toString());
                    msg.setUserId(userId);
                    httpPublisher.sendMessage(msg); // 자신을 포함한 모든 서버에 전송
                }).then();

        return Mono.when(outbound, inbound)
                .doFinally(sig -> {
                    roomManager.unsubscribeRoom(rooms, userId);
                });
    }

    private ChatMessage fromJson(String json) {
        try {
            return objectMapper.readValue(json, ChatMessage.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String toJson(ChatMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
