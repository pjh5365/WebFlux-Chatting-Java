package pjh5365.webfluxchattingjava.chat.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import pjh5365.webfluxchattingjava.chat.domain.ChatMessage;
import reactor.core.publisher.Mono;

/**
 * 이중화 서버로 전송하기 위한 서비스
 * @author : 박지혁
 * @since : 2026/01/18
 */
@Service
@RequiredArgsConstructor
public class HttpPublisher {

    private final WebClient webClient;
    private final List<String> serverList = List.of("http://localhost:8080", "http://localhost:8081");

    public void sendMessage(ChatMessage message) {

        serverList.forEach(server -> { // 모든 서버들에게 메시지 전송
            webClient.post()
                    .uri(server + "/chat/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(message)   // == @RequestBody
                    .retrieve()
                    .bodyToMono(ChatMessage.class).subscribe();
        });
    }

}
