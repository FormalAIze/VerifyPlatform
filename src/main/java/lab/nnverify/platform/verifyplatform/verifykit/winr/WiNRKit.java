package lab.nnverify.platform.verifyplatform.verifykit.winr;

import lab.nnverify.platform.verifyplatform.config.SessionManager;
import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lab.nnverify.platform.verifyplatform.verifykit.TaskExecuteListener;
import lab.nnverify.platform.verifyplatform.verifykit.verifast.VerifastResultManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WiNRKit {
    private WebSocketSession session = null;
    private ResultManager resultManager = new WiNRResultManager();
    private TaskExecuteListener taskExecuteListener = new TaskExecuteListener() {
        @Override
        public void beforeTaskExecute() {
            log.info("-----beforeWiNRTaskExecute-----");
        }

        @Override
        public void afterTaskExecute() {
            log.info("-----afterWiNRTaskExecute-----");
//            try {
//                sendResultFile();
//            } catch (IOException e) {
//                log.error("file io exception");
//                e.printStackTrace();
//            }
        }
    };

    // todo 异步的返回结果使用socket？
    private void sendResultFile() throws IOException {
        InputStreamReader file = resultManager.getResultFile();
        BufferedReader reader = new BufferedReader(file);
        String line;
        ArrayList<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
    }

    public List<String> sendResultFileSync() throws IOException {
        InputStreamReader file = resultManager.getResultFile();
        if (file == null) {
            return new ArrayList<>();
        }
        BufferedReader reader = new BufferedReader(file);
        String line;
        ArrayList<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    public List<String> sendAdvExample(int verifyId) {
        List<String> advExamples = resultManager.getAdvExample(verifyId);
        return advExamples;
    }

    public int testMock(String userId) {
        taskExecuteListener.beforeTaskExecute();
        new Thread(() -> {
            taskExecuteListener.beforeTaskExecute();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    public int testMockSync(String userId) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int test(String userId) {
        session = SessionManager.getSession(userId);
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

    public int testSync(String userId) {
        taskExecuteListener.beforeTaskExecute();
        task();
        taskExecuteListener.afterTaskExecute();
        return 1;
    }

    private void task() {
        int runStatus = 1;
        ProcessBuilder processBuilder = new ProcessBuilder("./run.sh");
        processBuilder.directory(new File("/home/GuoXingWu/WiNR_GXW"));
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            try {
                runStatus = process.waitFor();
                log.info("runStatus: " + runStatus);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
