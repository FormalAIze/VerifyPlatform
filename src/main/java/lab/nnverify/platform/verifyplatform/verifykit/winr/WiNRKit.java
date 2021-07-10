package lab.nnverify.platform.verifyplatform.verifykit.winr;

import lab.nnverify.platform.verifyplatform.config.SessionManager;
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
    public WiNRKit() {
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    private Map<String, Object> params = null;
    private WebSocketSession session = null;
    private WiNRResultManager wiNRResultManager = new WiNRResultManager();
    private int asyncCheck = 0;
    private TaskExecuteListener taskExecuteListener = new TaskExecuteListener() {
        @Override
        public void beforeTaskExecute() {
            log.info("-----beforeWiNRTaskExecute-----");
            asyncCheck++;
            log.info("the async check value is: " + asyncCheck);
        }

        @Override
        public void afterTaskExecute() {
            log.info("-----afterWiNRTaskExecute-----");
            // 创建一个锚点 方便之后通过verify_id查找文件
            String verifyId = (String) params.get("verifyId");
            log.info("verify id after task: " + verifyId);
            if (!wiNRResultManager.createResultFileAnchor(verifyId)) {
                log.error("anchor create failed: result file anchor create failed, verifyId is " + verifyId);
            }
            if (!wiNRResultManager.createAdvExampleAnchor(verifyId)) {
                log.error("anchor create failed: advExample file anchor create failed, verifyId is " + verifyId);
            }
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
        InputStreamReader file = wiNRResultManager.getResultFile("1");
        BufferedReader reader = new BufferedReader(file);
        String line;
        ArrayList<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
    }

    public Map<String, String> getResultSync() throws IOException {
        String verifyId = (String) params.get("verifyId");
        InputStreamReader file = wiNRResultManager.getResultFile(verifyId);
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

    public List<String> getAdvExample(int image_num) {
        String verifyId = (String) params.get("verifyId");

        List<String> advExamples = wiNRResultManager.getAdvExample(verifyId, image_num);
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
            task();
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    public int testSync(String userId) {
        taskExecuteListener.beforeTaskExecute();
        if (!paramsCheck()) {
            return -1;
        }
        task();
        taskExecuteListener.afterTaskExecute();
        return 1;
    }

    private boolean paramsCheck() {
        return true;
    }

    private void task() {
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
