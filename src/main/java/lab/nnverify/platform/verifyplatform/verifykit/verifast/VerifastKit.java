package lab.nnverify.platform.verifyplatform.verifykit.verifast;

import lab.nnverify.platform.verifyplatform.config.WebSocketSessionManager;
import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lab.nnverify.platform.verifyplatform.verifykit.TaskExecuteListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


@Slf4j
public class VerifastKit {
    private WebSocketSession session = null;
    private ResultManager resultManager = new VerifastResultManager();
    private TaskExecuteListener taskExecuteListener = new TaskExecuteListener() {
        @Override
        public void beforeTaskExecute() {
            log.info("-----beforeTaskExecute-----");
        }

        @Override
        public void afterTaskExecute() {
            log.info("-----afterTaskExecute-----");
            try {
                sendResultFile();
            } catch (IOException e) {
                log.error("file io exception");
                e.printStackTrace();
            }
        }
    };

    private void sendResultFile() throws IOException {
//        InputStreamReader file = resultManager.getResultFiles("1");
//        BufferedReader reader = new BufferedReader(file);
//        String line;
//        while ((line = reader.readLine()) != null) {
//            session.sendMessage(new TextMessage("resultFile---" + line));
//        }
    }

    public int testWithMIPVerifyMock(String userId) {
        session = WebSocketSessionManager.getSession(userId);
        if (session == null) {
            return -500;
        }
        taskExecuteListener.beforeTaskExecute();
        new Thread(() -> {
            taskExecuteListener.beforeTaskExecute();
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
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    public int testWithMIPVerify(String userId) {
        session = WebSocketSessionManager.getSession(userId);
        if (session == null) {
            return -500;
        }
        new Thread(() -> {
            taskExecuteListener.beforeTaskExecute();
            task();
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    private void task() {
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
