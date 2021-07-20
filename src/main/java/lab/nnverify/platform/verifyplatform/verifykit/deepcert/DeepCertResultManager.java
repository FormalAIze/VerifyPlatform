package lab.nnverify.platform.verifyplatform.verifykit.deepcert;

import lab.nnverify.platform.verifyplatform.verifykit.ResultManager;

public class DeepCertResultManager extends ResultManager {
    private final String basicPath = DeepCertConfig.basicPath;

    @Override
    public String getLogPath() {
        return basicPath;
    }

    @Override
    public String getAdvExamplePath() {
        return null;
    }
}
