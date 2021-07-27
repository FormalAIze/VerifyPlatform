package lab.nnverify.platform.verifyplatform.verifykit.winr;

import org.springframework.beans.factory.annotation.Value;

public class WiNRConfig {
    @Value(("${winr.file.upload.model}"))
    public static String uploadModelFilepathWiNR;

    @Value("${winr.file.upload.image}")
    public static String uploadImageFilepathWiNR;

    @Value("${winr.basicPath}")
    public static String basicPath;

}
