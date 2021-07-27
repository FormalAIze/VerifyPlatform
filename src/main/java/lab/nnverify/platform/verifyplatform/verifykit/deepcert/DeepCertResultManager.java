package lab.nnverify.platform.verifyplatform.verifykit.deepcert;

import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeepCertResultManager extends ResultManager {
    @Autowired
    DeepCertConfig deepCertConfig;

    @Override
    public String getLogPath() {
        return deepCertConfig.getBasicPath();
    }

    @Override
    public String getAdvExamplePath() {
        return null;
    }
}
