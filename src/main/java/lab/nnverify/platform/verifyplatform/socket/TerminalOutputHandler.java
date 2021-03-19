package lab.nnverify.platform.verifyplatform.socket;

import com.alibaba.fastjson.JSONObject;
import lab.nnverify.platform.verifyplatform.verifykit.VerifastKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;

@Slf4j
public class TerminalOutputHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("receive request");
        String messagePayload = message.getPayload();
        HashMap<String, String> map = JSONObject.parseObject(messagePayload, HashMap.class);
        log.info("-------------message received-------------");
        log.info(String.valueOf(map));
        String id = map.get("id");
        if (id.equals("1")) {
            VerifastKit.testWithMIPVerify(session);
        } else {
            session.sendMessage(new TextMessage("Hello from terminal"));
        }
    }
}
