package pjh5365.webfluxchattingjava.chat.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pjh5365.webfluxchattingjava.chat.domain.ChatMessage;
import pjh5365.webfluxchattingjava.chat.domain.ClientSession;

/**
 * 각 채팅방별 사용자들의 소켓을 관리하는 서비스
 * @author : 박지혁
 * @since : 2026/02/08
 */
@Slf4j
@Service
public class RoomManager {

    private final Map<String, List<ClientSession>> roomClients = new ConcurrentHashMap<>(); // 각 채팅방별 참여자를 가지고 있는 해시맵

    public void emit(String chatroomId, ChatMessage chatMessage) { // 각 채팅방의 Sinks로 메시지를 전송한다.
        List<ClientSession> roomSession = getRoomSession(chatroomId);
        if (roomSession.isEmpty()) { // 사용자 정보가 없다면 해당 채팅방에 활성화된 사용자가 없음
            return;
        }
        roomSession.forEach(client -> client.sendMessage(chatMessage));
    }

    public void subscribe(List<String> rooms, ClientSession clientSession) { // 사용자가 참여중인 모든 채팅방에 해당 사용자 세션 추가
        rooms.forEach(room -> getRoomSession(room).add(clientSession)); // 채팅방별 리스트에 사용자 세션 추가
    }

    public void unsubscribeRoom(List<String> rooms, String userId) {
        rooms.forEach(room -> roomClients.put(room, new CopyOnWriteArrayList<>(getRoomSession(room).stream()
                .filter(session -> !session.getUserId().equals(userId)) // 제거할 사용자가 빠진 리스트로 교체
                .toList())));
    }

    private List<ClientSession> getRoomSession(String chatroomId) { // 채팅방에 연결된 사용자 정보를 가져온다.
        return roomClients.computeIfAbsent(chatroomId, id -> new CopyOnWriteArrayList<>());
    }
}
