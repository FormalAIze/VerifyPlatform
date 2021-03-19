package lab.nnverify.platform.verifyplatform.config;

import lab.nnverify.platform.verifyplatform.socket.TestHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(getTestHandler(), "hello/{ID}")
                .setAllowedOrigins("*");
    }

    public WebSocketHandler getTestHandler() {
        return new TestHandler();
    }
}
