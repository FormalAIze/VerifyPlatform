package lab.nnverify.platform.verifyplatform.verifykit.winr;

import lab.nnverify.platform.verifyplatform.config.SessionManager;
import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lab.nnverify.platform.verifyplatform.verifykit.TaskExecuteListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, String> sendResultSync() throws IOException {
        InputStreamReader file = resultManager.getResultFile();
        if (file == null) {
            return new HashMap<>();
        }
        BufferedReader reader = new BufferedReader(file);
        String line;
        ArrayList<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        String[] secondLastLine = result.get(result.size() - 2).split("\\s+");
        String[] lastLine = result.get(result.size() - 1).split("\\s+");
        HashMap<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < secondLastLine.length; i++) {
            String key = secondLastLine[i];
            resultMap.put(key, lastLine[i]);
        }
        return resultMap;
    }

    public List<String> sendAdvExample(int verifyId, int image_num) {
        List<String> advExamples = resultManager.getAdvExample(verifyId, image_num);
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
            Thread.sleep(500);
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
            task(null);
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    public int testSync(String userId, Map<String, Object> params) {
        taskExecuteListener.beforeTaskExecute();
        if (!paramsCheck(params)) {
            return -1;
        }
        task(params);
        taskExecuteListener.afterTaskExecute();
        return 1;
    }

    private boolean paramsCheck(Map<String, Object> params) {
        return true;
    }

    private void task(Map<String, Object> params) {
        int runStatus = 1;
        String dataset = (String) params.get("dataset");
        String epsilon = (String) params.get("epsilon");
        String model = (String) params.get("model");
        String imageNum = (String) params.get("imageNum");

        try {
            PrintWriter printWriter = new PrintWriter(WiNRConfig.basicPath + "run.sh");
            String command = String.format(
                    "python main.py --netname %s --epsilon %s --dataset %s --num_image %s",
                    model, epsilon, dataset, imageNum);
            log.info("the command is " + command);
            printWriter.write(command);
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("fail to write into run.sh");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("./run.sh");
        processBuilder.directory(new File(WiNRConfig.basicPath));
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
