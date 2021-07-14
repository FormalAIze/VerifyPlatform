package lab.nnverify.platform.verifyplatform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
public class WebSocketSessionManager {
    private static HashMap<String, WebSocketSession> sessionPool = new HashMap<>();

    public static void addSession(String id, WebSocketSession session) {
        sessionPool.put(id, session);
        log.info("session add to session pool, uri: " + session.getUri());
    }

    public static void deleteSession(WebSocketSession session) {
        Collection<WebSocketSession> values = sessionPool.values();
        values.remove(session);
        log.info("session removed from pool, uri: " + session.getUri());
        log.info("current item count in pool: " + sessionPool.size());
    }

    public static WebSocketSession getSession(String id) {
        return sessionPool.get(id);
    }
}
