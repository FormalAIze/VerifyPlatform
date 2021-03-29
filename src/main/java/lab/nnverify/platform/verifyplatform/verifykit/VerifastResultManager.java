package lab.nnverify.platform.verifyplatform.verifykit;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

@Slf4j
public class VerifastResultManager extends ResultManager{
    //todo 部署是更换
    private String resultFilepath = "/home/GuoXingWu/mipverify/result/fnn1_0.003921_20210317_test.txt";
    private String resultFilepathMock = "./mock.txt";

    @Override
    InputStreamReader getResultFile() {
        try {
            return new InputStreamReader(new FileInputStream(resultFilepathMock));
        } catch (FileNotFoundException e) {
            log.error("file not found, filepath: " + resultFilepathMock);
            e.printStackTrace();
        }
        return null;
    }
}
