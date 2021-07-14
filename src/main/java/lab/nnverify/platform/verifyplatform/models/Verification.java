package lab.nnverify.platform.verifyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Verification {
    private String verifyId;
    private int userId;
    private String tool;
    private String epsilon;
    private String netName;
    private String dataset;
    private String numOfImage;
    private String status;
}
