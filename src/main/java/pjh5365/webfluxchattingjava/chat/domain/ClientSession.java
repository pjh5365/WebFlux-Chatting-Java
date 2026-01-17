package pjh5365.webfluxchattingjava.chat.domain;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * 웹 소켓에 연결한 클라이언트의 세션 정보
 * @author : 박지혁
 * @since : 2026/01/16
 */
@RequiredArgsConstructor
public class ClientSession {

    private static final Duration TTL = Duration.ofSeconds(30); // 30초간의 대기 시간
    private final String userId; // 사용자 정보
    private final Sinks.Many<ChatMessage> clientSinks = Sinks.many().unicast().onBackpressureBuffer(); // 사용자가 참가중인 모든 방의 Sinks를 전부 구독해 한번에 받을 Sinks
    private final Map<String, Disposable> roomSubscribeInfo = new ConcurrentHashMap<>(); // 각 채팅방별 구독종료 메서드를 가지고 있는 해시맵 (채팅방에서 나갈때 해당 작업을 수행하면 구독을 해제할 수 있다)

    @Getter
    private volatile ClientStatus state = ClientStatus.CONNECTED;
    private volatile long waitingUntil = 0L;

    public void subscribeRoom(String chatroomId, Flux<ChatMessage> roomFlux) {
        Disposable disposable = roomFlux.subscribe(clientSinks::tryEmitNext); // 특정 채팅방의 Flux를 구독하고 메시지가 오면 clientSinks로 전송한다.

        roomSubscribeInfo.put(chatroomId, disposable);
    }

    public void unsubscribeRoom(String chatroomId) {
        Disposable disposable = roomSubscribeInfo.remove(chatroomId);
        if (disposable != null) { // 구독 정보가 남아있다면
            disposable.dispose(); // 구독 종료 (해당 채팅방 나가기 처리)
        }
    }

    public void onDisconnect() {
        state = ClientStatus.WAITING; // 연결이 끊기면 대기 상태로 변경
        waitingUntil = System.currentTimeMillis() + TTL.toMillis(); // 대기할 시간 설정
    }

    public boolean canReconnect() { // 재연결이 가능한 상태인가
        return state == ClientStatus.WAITING && System.currentTimeMillis() <= waitingUntil;
    }

    public void onReconnect() {
        state = ClientStatus.CONNECTED;
    }

    public void closeIfExpire() {
        if (state == ClientStatus.WAITING && System.currentTimeMillis() > waitingUntil) {
            close();
        }
    }

    public void close() { // 클라이언트가 소켓을 종료할때
        state = ClientStatus.CLOSED;
        roomSubscribeInfo.values().forEach(Disposable::dispose); // 구독중인 모든 채팅방을 구독종료하고
        clientSinks.tryEmitComplete(); // Sinks를 종료한다.
    }

    public Flux<ChatMessage> getFlux() { // 사용자의 Sinks 반환
        return clientSinks.asFlux();
    }
}
