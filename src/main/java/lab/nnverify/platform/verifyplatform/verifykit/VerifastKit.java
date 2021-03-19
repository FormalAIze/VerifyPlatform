package lab.nnverify.platform.verifyplatform.verifykit;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class VerifastKit {
    public static int testWithMIPVerify(WebSocketSession session) {
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
                System.out.println(runStatus);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return runStatus;
    }
}
