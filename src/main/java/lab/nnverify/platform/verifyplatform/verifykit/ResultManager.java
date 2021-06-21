package lab.nnverify.platform.verifyplatform.verifykit;

import java.io.InputStreamReader;
import java.util.List;

abstract public class ResultManager {
    public abstract InputStreamReader getResultFile(String verifyId);

    public abstract List<String> getAdvExample(String verifyId, int image_num);
}
