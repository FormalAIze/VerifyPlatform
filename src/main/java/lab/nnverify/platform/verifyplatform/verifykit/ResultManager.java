package lab.nnverify.platform.verifyplatform.verifykit;

import java.io.InputStreamReader;
import java.util.List;

abstract public class ResultManager {
    public abstract InputStreamReader getResultFile();

    public abstract List<String> getAdvExample(int verifyId, int image_num);
}
