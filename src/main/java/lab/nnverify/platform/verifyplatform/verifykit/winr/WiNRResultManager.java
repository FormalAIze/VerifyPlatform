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
    public InputStreamReader getResultFile() {
        // todo 目前直接使用最新产生的log文件 因为不知道文件名和执行命令的对应关系
        ProcessBuilder processBuilder = new ProcessBuilder("ls", "-at");
        processBuilder.directory(new File(basicPath + "logs"));
        String filename = null;
        processBuilder.redirectErrorStream(true); // 需要把输出和错误流重定向 否则大量向缓冲区写入会导致死锁
        try {
            Process process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            // 使用最新生成的一个log文件
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
    public List<String> getAdvExample(int verifyId, int image_num) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls", "-at");
        processBuilder.directory(new File(basicPath + "adv_examples"));
        List<String> filenames = new ArrayList<>();
        processBuilder.redirectErrorStream(true); // 需要把输出和错误流重定向 否则大量向缓冲区写入会导致死锁
        try {
            Process process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            for (int i = 0; i < image_num && ((line = input.readLine()) != null); i++) {
                filenames.add(line);
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
