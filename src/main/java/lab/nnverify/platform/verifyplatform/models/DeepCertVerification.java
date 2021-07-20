package lab.nnverify.platform.verifyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepCertVerification {
    private String verifyId;
    private int userId;
    private String tool;

    private String netName;
    private String numOfImage;
    private String norm;
    private String core;
    private String activation;
    private String isCifar;
    private String isTinyImageNet;

    // 四种状态: ready, running, success, error
    private String status;
    private Timestamp startTime;
}
