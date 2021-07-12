package lab.nnverify.platform.verifyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBody {
    private int status;
    private Map<String, Object> data = new HashMap<>();
}
