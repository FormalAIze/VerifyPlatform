package lab.nnverify.platform.verifyplatform.verifykit;

import lab.nnverify.platform.verifyplatform.config.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


@Slf4j
public class VerifastKit {

    public static int testWithMIPVerifyMock(String userId) {
        WebSocketSession session = SessionManager.getSession(userId);
        if (session == null) {
            return -500;
        }
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 100; i++) {
                try {
                    session.sendMessage(new TextMessage("message " + i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return 1;
    }

    public static int testWithMIPVerify(String userId) {
        WebSocketSession session = SessionManager.getSession(userId);
        if (session == null) {
            return -500;
        }
        new Thread(() -> task(session)).start();
        return 1;
    }

    private static void task(WebSocketSession session) {
        int runStatus = 1;
        ProcessBuilder processBuilder = new ProcessBuilder("./run_fnn1_validation.sh");
        processBuilder.directory(new File("/home/GuoXingWu/mipverify"));
        processBuilder.redirectErrorStream(true); // 需要把输出和错误流重定向 否则大量向缓冲区写入会导致死锁
        try {
            Process process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = input.readLine()) != null) {
                if (session != null) {
                    session.sendMessage(new TextMessage(s));
                } else {
                    System.out.println(s);
                }
            }
            while ((s = error.readLine()) != null) {
                if (session != null) {
                    session.sendMessage(new TextMessage(s));
                } else {
                    System.out.println(s);
                }
            }
            try {
                runStatus = process.waitFor();
                if (session != null) {
                    session.sendMessage(new TextMessage(String.valueOf(runStatus)));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
