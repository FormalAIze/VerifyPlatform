package lab.nnverify.platform.verifyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WiNRVerification {
    private String verifyId;
    private int userId;
    private String tool;

    private String epsilon;
    private String netName;
    private String dataset;
    private Map<String, String> testImageInfo;

    // 四种状态: ready, running, success, error
    private String status;
    private Timestamp startTime;
}
