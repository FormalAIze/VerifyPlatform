package lab.nnverify.platform.verifyplatform.verifykit.deepcert;

import lab.nnverify.platform.verifyplatform.config.WebSocketSessionManager;
import lab.nnverify.platform.verifyplatform.models.DeepCertVerification;
import lab.nnverify.platform.verifyplatform.services.VerificationService;
import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lab.nnverify.platform.verifyplatform.verifykit.TaskExecuteListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个类需要是线程安全的
 */
@Slf4j
@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DeepCertKit {
    @Autowired
    VerificationService verificationService;

    @Autowired
    DeepCertConfig deepCertConfig;

    @Autowired
    @Qualifier("deepCertResultManager") ResultManager deepCertResultManager;

    public DeepCertKit() {
    }

    private int runStatus = 1;

    private int asyncCheck = 0;

    private WebSocketSession session = null;

    private DeepCertVerification params = null;

    private TaskExecuteListener taskExecuteListener = new TaskExecuteListener() {
        @Override
        public void beforeTaskExecute() {
            log.info("-----beforeTaskExecute-----");
            asyncCheck++;
            params.setStatus("running");
            if (verificationService.saveDeepCertVerificationParams(params)) {
                log.info("write verification record to database");
            } else {
                log.error("fail to write verification record to database");
            }
            // 测试照片信息存入数据库
            int successSaveCount = verificationService.saveTestImageOfVerification(params.getVerifyId(), params.getTestImageInfo());
            log.info("verification test images saved. " + successSaveCount + "/" + params.getTestImageInfo().keySet().size());
            log.info("the async check value is: " + asyncCheck);
        }

        @Override
        public void afterTaskExecute() {
            log.info("-----afterTaskExecute-----");
            String verifyId = params.getVerifyId();
            log.info("verify id after task: " + verifyId);
            if (runStatus == 0) {
                verificationService.finishVerificationUpdateStatus(verifyId, "success");
                try {
                    if (session != null) {
                        session.sendMessage(new TextMessage("verify success. verifyId:" + verifyId));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                verificationService.finishVerificationUpdateStatus(verifyId, "error");
                try {
                    if (session != null) {
                        session.sendMessage(new TextMessage("verify failed. verifyId:" + verifyId));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void setParams(DeepCertVerification params) {
        this.params = params;
    }

    public List<String> getOriginImages() {
        String verifyId = params.getVerifyId();
        return deepCertResultManager.getOriginImages(verifyId);
    }

    public Map<String, Map<String, String>> getResultSync() throws IOException {
        String verifyId = params.getVerifyId();
        if (!params.getStatus().equals("success")) {
            return new HashMap<>();
        }
        List<InputStreamReader> file = deepCertResultManager.getResultFiles(verifyId);
        if (file.size() == 0) {
            return new HashMap<>();
        }
        // deepcert only has one log file per verification
        BufferedReader reader = new BufferedReader(file.get(0));
        String line;
        ArrayList<String> dataGen = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("[DATAGEN]")) {
                dataGen.add(line);
            } else {
                result.add(line);
            }
        }
        HashMap<String, Map<String, String>> map = new HashMap<>();
        HashMap<String, String[]> dataGenMap = new HashMap<>();
        HashMap<String, String[]> resultMap = new HashMap<>();

        for (String s : dataGen) {
            String[] split = s.split(",");
            dataGenMap.put(split[1].trim().split("=")[1].trim(), split);
        }
        for (String s : result) {
            String[] split = s.split(",");
            resultMap.put(split[2].trim().split("=")[1].trim(), split);
        }

        for (String id : dataGenMap.keySet()) {
            String[] split = dataGenMap.get(id);
            if (split[4].trim().split("=")[1].trim().equalsIgnoreCase("true")) {
                String[] split1 = resultMap.get(id);
                HashMap<String, String> map1 = new HashMap<>();
                for (int i = 1; i < split1.length; i++) {
                    String s = split1[i];
                    String[] split2 = s.trim().split("=");
                    map1.put(split2[0].trim().replace(" ", "_"), split2[1].trim());
                }
                String[] split2 = split[0].trim().split("=");
                String[] split3 = split2[1].trim().split("/");
                map1.put(split2[0].trim().substring(13).trim(), split3[split3.length - 1].trim());
                map.put(id, map1);
            } else {
                map.put(id, new HashMap<>());
            }
        }
        return map;
    }

    public int testAsync() {
        session = WebSocketSessionManager.getSession(String.valueOf(params.getUserId()));
        new Thread(() -> {
            taskExecuteListener.beforeTaskExecute();
            task();
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    private void task() {
        // 这里写死为models/... 上传路径即为该路径
        String netName = "models/" + params.getNetName();
        // todo 如果出现拷贝文件失败的情况 这里的值是会大于实际的图片数量的
        String numOfImage = String.valueOf(params.getTestImageInfo().keySet().size());
        String norm = params.getNorm();
        String jsonPath = params.getJsonPath().replace(" ", "\\ ");

        try {
            PrintWriter printWriter = new PrintWriter(deepCertConfig.getBasicPath() + "run.sh");
            // python pymain.py models/mnist_cnn_8layer_5_3_sigmoid 10 i True sigmoid False False
            String command = String.format(
                    "python pymain.py %s %s %s %s %s",
                    params.getVerifyId(), netName, numOfImage, norm, jsonPath);
            log.info("the command is " + command);
            ArrayList<String> commends = new ArrayList<>();
            commends.add("# >>> conda initialize >>>");
            commends.add("# !! Contents within this block are managed by 'conda init' !!");
            commends.add("__conda_setup=\"$('/home/GuoXingWu/anaconda3/bin/conda' 'shell.bash' 'hook' 2> /dev/null)\"");
            commends.add("if [ $? -eq 0 ]; then");
            commends.add("    eval \"$__conda_setup\"");
            commends.add("else");
            commends.add("    if [ -f \"/home/GuoXingWu/anaconda3/etc/profile.d/conda.sh\" ]; then");
            commends.add("        . \"/home/GuoXingWu/anaconda3/etc/profile.d/conda.sh\"");
            commends.add("    else");
            commends.add("        export PATH=\"/home/GuoXingWu/anaconda3/bin:$PATH\"");
            commends.add("    fi");
            commends.add("fi");
            commends.add("unset __conda_setup");
            commends.add("# <<< conda initialize <<<");
            commends.add("conda activate deepcert");
            commends.add(command);
            for (String s : commends) {
                printWriter.println(s);
            }
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("fail to write into run.sh");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("./run.sh");
        processBuilder.directory(new File(deepCertConfig.getBasicPath()));
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
