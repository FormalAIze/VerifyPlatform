package lab.nnverify.platform.verifyplatform.verifykit.deepcert;

import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeepCertResultManager extends ResultManager {
    @Autowired
    DeepCertConfig deepCertConfig;

    @Override
    public String getLogPath(String verifyId) {
        return deepCertConfig.getLogBasePath() + verifyId + "/";
    }

    @Override
    public String getAdvExamplePath(String verifyId) {
        return null;
    }

    @Override
    public String getOriginImagePath(String verifyId) {
        return deepCertConfig.getOriginImageBasePath() + verifyId + "/";
    }

    @Override
    public List<String> getOriginImages(String verifyId) {
        return getImages(getOriginImagePath(verifyId));
    }
}
