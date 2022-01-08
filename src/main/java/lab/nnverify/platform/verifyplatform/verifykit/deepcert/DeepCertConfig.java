package lab.nnverify.platform.verifyplatform.verifykit.deepcert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepCertConfig {
    @Value("${deepcert.basicPath}")
    private String basicPath ;

    @Value("${deepcert.file.upload.image}")
    private String uploadImageFilepath;

    @Value("${deepcert.file.upload.model}")
    private String uploadModelFilepath;

    @Value("${deepcert.result.image.origin}")
    private String originImageBasePath;

    @Value("${deepcert.result.log}")
    private String logBasePath;

    @Value("${deepcert.imageinfo.json.file}")
    private String jsonPath;
}
