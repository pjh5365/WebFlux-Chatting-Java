package pjh5365.webfluxchattingjava.chat.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import lombok.RequiredArgsConstructor;
import pjh5365.webfluxchattingjava.chat.handler.SocketHandler;

/**
 * 소켓 설정
 * @author : 박지혁
 * @since : 2026/01/16
 */
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final SocketHandler socketHandler;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/chat", socketHandler); // /ws/chat로 들어오면 웹소켓으로 매핑한다.

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
