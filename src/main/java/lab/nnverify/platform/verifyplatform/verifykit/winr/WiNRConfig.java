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

}
