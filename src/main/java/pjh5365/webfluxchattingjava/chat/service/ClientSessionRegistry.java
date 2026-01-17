package pjh5365.webfluxchattingjava.chat.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pjh5365.webfluxchattingjava.chat.domain.ClientSession;

/**
 * 클라이언트 세션 재연결을 관리하는 서비스
 * @author : 박지혁
 * @since : 2026/01/17
 */
@Service
public class ClientSessionRegistry {

    private final Map<String, ClientSession> sessions = new ConcurrentHashMap<>(); // 사용자별 세션을 관리하는 해시맵

    public ClientSession getOrCreate(String userId) { // 클라이언트의 세션을 만들거나 기존에 가지고 있는 세션을 반환한다.
        return sessions.computeIfAbsent(userId, ClientSession::new);
    }

    public void onDisconnect(String userId) { // 사용자의 연결이 끊어지면
        ClientSession session = sessions.get(userId);
        if (session != null) {
            session.onDisconnect(); // 세션을 종료한다 (대기상태로 넘긴다)
        }
    }

    public void onReconnect(String userId) {
        ClientSession session = sessions.get(userId);
        if (session != null && session.canReconnect()) {
            session.onReconnect();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void cleanUp() { // 5초마다 만료된 세션 만료처리
        sessions.values().forEach(ClientSession::closeIfExpire);
    }
}
