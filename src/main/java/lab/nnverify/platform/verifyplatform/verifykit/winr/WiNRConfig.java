package lab.nnverify.platform.verifyplatform.verifykit.winr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WiNRConfig {
    @Value(("${winr.file.upload.model}"))
    private String uploadModelFilepath;

    @Value("${winr.file.upload.image}")
    private String uploadImageFilepath;

    @Value("${winr.basicPath}")
    private String basicPath;

    @Value("${winr.imageinfo.json.file}")
    private String jsonPath;

    @Value("${winr.result.image.origin}")
    private String originImageBasePath;

    @Value("${winr.result.image.adv}")
    private String advImageBasePath;

    @Value("${winr.result.log}")
    private String logBasePath;
}
