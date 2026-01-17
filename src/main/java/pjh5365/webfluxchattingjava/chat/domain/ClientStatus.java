package pjh5365.webfluxchattingjava.chat.domain;

/**
 * 클라이언트 상태값
 * @author : 박지혁
 * @since : 2026/01/17
 */
public enum ClientStatus {
    CONNECTED, // 연결상태
    WAITING, // 대기상태
    CLOSED // 종료상태
}
