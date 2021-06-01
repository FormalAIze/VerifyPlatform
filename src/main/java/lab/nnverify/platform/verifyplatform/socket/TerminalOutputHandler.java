package lab.nnverify.platform.verifyplatform.socket;

import com.alibaba.fastjson.JSONObject;
import lab.nnverify.platform.verifyplatform.config.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class TerminalOutputHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(session.getId());
        log.info(String.valueOf(session.getPrincipal()));
        // session.id: unique string
        // session.uri: ws://localhost:9090/terminal/ID=888
        if (session.getUri() == null) {
            log.info("no user id");
            session.close();
        } else {
            List<String> ids = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().get("id");
            String userId = ids.get(0);
            log.info("userId: " + userId);
            SessionManager.addSession(userId, session);
            log.info("connection established");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed");
        SessionManager.deleteSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("receive request");
        String messagePayload = message.getPayload();
        HashMap<String, String> map = JSONObject.parseObject(messagePayload, HashMap.class);
        log.info("-------------message received-------------");
        log.info(String.valueOf(map));
//        String id = map.get("id");
//        if (id.equals("1")) {
//            VerifastKit.testWithMIPVerify("0");
//        } else {
//            session.sendMessage(new TextMessage("Hello from terminal"));
//        }
    }
}
