package lab.nnverify.platform.verifyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private int status;
    private final String token;
}
