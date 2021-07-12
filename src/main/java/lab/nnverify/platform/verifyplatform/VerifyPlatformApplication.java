package lab.nnverify.platform.verifyplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("lab.nnverify.platform.verifyplatform.mapper")
public class VerifyPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerifyPlatformApplication.class, args);
    }

}
