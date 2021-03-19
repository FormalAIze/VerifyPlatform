package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.verifykit.VerifastKit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VerifastController {

    @RequestMapping("/verifast/mipverify")
    public void VerifastUseMIPVerify() {
        VerifastKit.testWithMIPVerify(null);
    }

    @RequestMapping("/verifast/test")
    public String test() {
        return "socket_test";
    }
}
