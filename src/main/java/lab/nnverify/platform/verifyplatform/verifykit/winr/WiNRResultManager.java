package lab.nnverify.platform.verifyplatform.verifykit.winr;

import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WiNRResultManager extends ResultManager {

    @Autowired
    WiNRConfig wiNRConfig;

    @Override
    public String getLogPath(String verifyId) {
        return wiNRConfig.getLogBasePath() + verifyId + "/";
    }

    @Override
    public String getAdvExamplePath(String verifyId) {
        return wiNRConfig.getAdvImageBasePath() + verifyId + "/";
    }

    @Override
    public String getOriginImagePath(String verifyId) {
        return wiNRConfig.getOriginImageBasePath() + verifyId + "/";
    }

    @Override
    public List<String> getAdvExample(String verifyId) {
        return getImages(getAdvExamplePath(verifyId));
    }

    @Override
    public List<String> getOriginImages(String verifyId) {
        return getImages(getOriginImagePath(verifyId));
    }
}
