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
            // 创建一个锚点 方便之后通过verify_id查找文件
            String verifyId = params.getVerifyId();
            log.info("verify id after task: " + verifyId);
            if (!deepCertResultManager.createResultFileAnchor(verifyId)) {
                log.error("anchor create failed: result file anchor create failed, verifyId is " + verifyId);
            }
            if (runStatus == 0) {
                verificationService.finishVerificationUpdateStatus(verifyId, "success");
                try {
                    session.sendMessage(new TextMessage("verify success. verifyId:" + verifyId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                verificationService.finishVerificationUpdateStatus(verifyId, "error");
                try {
                    session.sendMessage(new TextMessage("verify failed. verifyId:" + verifyId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void setParams(DeepCertVerification params) {
        this.params = params;
    }

    public Map<String, String> getResultSync() throws IOException {
        String verifyId = params.getVerifyId();
        if (!params.getStatus().equals("success")) {
            return new HashMap<>();
        }
        InputStreamReader file = deepCertResultManager.getResultFile(verifyId);
        if (file == null) {
            return new HashMap<>();
        }
        BufferedReader reader = new BufferedReader(file);
        String line;
        ArrayList<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        HashMap<String, String> resultMap = new HashMap<>();

        String[] split = result.get(1).split(",");
        for (String s : split) {
            String[] split1 = s.trim().split("=");
            resultMap.put(split1[0].trim().replace(" ", "_"), split1[1].trim());
        }
        for (int i = 2; i < result.size(); i++) {
            String[] split1 = result.get(i).split("=");
            resultMap.put(split1[0].trim().replace(" ", "_"), split1[1].trim());
        }
        return resultMap;
    }

    public int testAsync() {
        session = WebSocketSessionManager.getSession(String.valueOf(params.getUserId()));
        // 没有获取到websocket session，完成执行之后无法通知浏览器端，目前先直接返回错误
        if (session == null) {
            return -100;
        }
        new Thread(() -> {
            taskExecuteListener.beforeTaskExecute();
            task();
            taskExecuteListener.afterTaskExecute();
        }).start();
        return 1;
    }

    private void task() {
        String netName = params.getNetName();
        String core = params.getCore();
        // todo 改回真实数据
        String numOfImage = "2";
        String norm = params.getNorm();
        String activation = params.getActivation();
        String isCifar = params.getIsCifar();
        String isTinyImageNet = params.getIsTinyImageNet();

        try {
            PrintWriter printWriter = new PrintWriter(deepCertConfig.getBasicPath() + "run.sh");
            // python pymain.py models/mnist_cnn_8layer_5_3_sigmoid 10 i True sigmoid False False
            String command = String.format(
                    "python pymain.py %s %s %s %s %s %s %s",
                    netName, numOfImage, norm, core, activation, isCifar, isTinyImageNet);
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
