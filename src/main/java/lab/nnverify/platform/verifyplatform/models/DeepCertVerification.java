package lab.nnverify.platform.verifyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepCertVerification {
    private String verifyId;
    private int userId;
    private String tool;

    private String netName;
    private Map<String, String> testImageInfo;
    private String norm;
    private String core;
    private String activation;
    private String isCifar;
    private String isTinyImageNet;
    private String jsonPath;

    // 四种状态: ready, running, success, error
    private String status;
    private Timestamp startTime;
}
