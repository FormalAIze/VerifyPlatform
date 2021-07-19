package lab.nnverify.platform.verifyplatform.verifykit;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
abstract public class ResultManager {
    public abstract String getLogPath();

    public abstract String getAdvExamplePath();

    public InputStreamReader getResultFile(String verifyId) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls", "-at");
        processBuilder.directory(new File(getLogPath()));
        String filename = null;
        processBuilder.redirectErrorStream(true); // 需要把输出和错误流重定向 否则大量向缓冲区写入会导致死锁
        try {
            Process process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            // 根据verifyId生成的锚点选择log文件
            while ((s = input.readLine()) != null) {
                if (s.equals("verify_" + verifyId)) {
                    break;
                }
            }
            filename = input.readLine();
            try {
                int runStatus = process.waitFor();
                System.out.println(runStatus);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (filename != null) {
            try {
                return new InputStreamReader(new FileInputStream(getLogPath() + filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<String> getAdvExample(String verifyId, int image_num) {
        return new ArrayList<>();
    }

    public boolean createResultFileAnchor(String verifyId) {
        String path = getLogPath();
        return createAnchor(path, verifyId);
    }

    public boolean createAdvExampleAnchor(String verifyId) {
        String path = getAdvExamplePath();
        return createAnchor(path, verifyId);
    }

    private boolean createAnchor(String path, String verifyId) {
        int runStatus = 1;
        ProcessBuilder processBuilder = new ProcessBuilder("touch", "verify_" + verifyId);
        processBuilder.directory(new File(path));
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            try {
                runStatus = process.waitFor();
                log.info("create anchor run status: " + runStatus);
                return runStatus == 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
