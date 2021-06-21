package lab.nnverify.platform.verifyplatform.verifykit.winr;

import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WiNRResultManager extends ResultManager {
    private final String basicPath = WiNRConfig.basicPath;

    @Override
    public InputStreamReader getResultFile(String verifyId) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls", "-at");
        processBuilder.directory(new File(basicPath + "logs"));
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

        log.info("log filename: " + filename);
        if (filename != null) {
            try {
                return new InputStreamReader(new FileInputStream(basicPath + "/logs/" + filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<String> getAdvExample(String verifyId, int image_num) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls", "-at");
        processBuilder.directory(new File(basicPath + "adv_examples"));
        List<String> filenames = new ArrayList<>();
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
            String line;
            for (int i = 0; i < image_num && ((line = input.readLine()) != null); i++) {
                filenames.add(line);
                log.info("adv example #" + i + " filename: " + line);
            }
            try {
                int runStatus = process.waitFor();
                System.out.println(runStatus);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String filename : filenames) {
            log.info("image filename: " + filename);
        }
        return filenames;
    }

    public boolean createResultFileAnchor(String verifyId) {
        String path = basicPath + "logs";
        return createAnchor(path, verifyId);
    }

    public boolean createAdvExampleAnchor(String verifyId) {
        String path = basicPath + "adv_examples";
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
