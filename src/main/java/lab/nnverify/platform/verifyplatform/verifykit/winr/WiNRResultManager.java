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
    public String getLogPath() {
        return basicPath + "logs/";
    }

    @Override
    public String getAdvExamplePath() {
        return basicPath + "adv_examples/";
    }

    @Override
    public List<String> getAdvExample(String verifyId, int image_num) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls", "-at");
        processBuilder.directory(new File(getAdvExamplePath()));
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
}
