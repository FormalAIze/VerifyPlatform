package lab.nnverify.platform.verifyplatform.socket;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class TestHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messagePayload = message.getPayload();
        HashMap<String, String> map = JSONObject.parseObject(messagePayload, HashMap.class);
        System.out.println("-------------message received-------------");
        System.out.println(map);
        session.sendMessage(new TextMessage("echo: " + messagePayload));
    }
}
