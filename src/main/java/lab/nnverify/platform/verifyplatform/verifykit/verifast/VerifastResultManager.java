package lab.nnverify.platform.verifyplatform.verifykit.verifast;

import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class VerifastResultManager extends ResultManager {
    //todo 部署是更换
    private String resultFilepath = "/home/GuoXingWu/mipverify/result/fnn1_0.003921_20210317_test.txt";
    private String resultFilepathMock = "./mock.txt";

    @Override
    public InputStreamReader getResultFile(String verifyId) {
        try {
            return new InputStreamReader(new FileInputStream(resultFilepathMock));
        } catch (FileNotFoundException e) {
            log.error("file not found, filepath: " + resultFilepathMock);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getAdvExample(String verifyId, int image_num) {
        return null;
    }
}
