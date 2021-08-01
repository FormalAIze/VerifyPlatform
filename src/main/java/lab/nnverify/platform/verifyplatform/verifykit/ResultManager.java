package lab.nnverify.platform.verifyplatform.verifykit;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
abstract public class ResultManager {
    public abstract String getLogPath(String verifyId);

    public abstract String getAdvExamplePath(String verifyId);

    public abstract String getOriginImagePath(String verifyId);

    public List<InputStreamReader> getResultFiles(String verifyId) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls");
        processBuilder.directory(new File(getLogPath(verifyId)));
        List<String> filenames = new ArrayList<>();
        processBuilder.redirectErrorStream(true); // 需要把输出和错误流重定向 否则大量向缓冲区写入会导致死锁
        try {
            Process process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = input.readLine()) != null) {
                filenames.add(s);
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
        List<InputStreamReader> readers = new ArrayList<>();
        if (filenames.size() > 0) {
            for (String filename : filenames) {
                try {
                    readers.add(new InputStreamReader(new FileInputStream(getLogPath(verifyId) + filename)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    log.error("log file load failed, filename: " + filename);
                }
            }
        }
        return readers;
    }

    public List<String> getAdvExample(String verifyId) {
        return new ArrayList<>();
    }

    public boolean createResultFileAnchor(String verifyId) {
        String path = getLogPath(verifyId);
        return createAnchor(path, verifyId);
    }

    public boolean createAdvExampleAnchor(String verifyId) {
        String path = getAdvExamplePath(verifyId);
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

    public abstract List<String> getOriginImages(String verifyId);

    protected List<String> getImages(String path) {
        ProcessBuilder processBuilder = new ProcessBuilder("ls");
        processBuilder.directory(new File(path));
        List<String> filenames = new ArrayList<>();
        processBuilder.redirectErrorStream(true); // 需要把输出和错误流重定向 否则大量向缓冲区写入会导致死锁
        try {
            Process process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = input.readLine()) != null) {
                filenames.add(s);
            }
            try {
                int runStatus = process.waitFor();
                log.info("run status: " + runStatus);
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
