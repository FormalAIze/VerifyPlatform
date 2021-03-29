package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.verifykit.VerifastKit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VerifastController {

    @RequestMapping("/verifast")
    public String verifastIndex() {
        return "socket_test";
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @RequestMapping("/verifast/mipverify/{userId}")
    public String verifastUseMIPVerify(@PathVariable String userId) {
        VerifastKit verifastKit = new VerifastKit();
        int status = verifastKit.testWithMIPVerify(userId);
        if (status > 0) {
            return "start running successfully";
        } else {
            return "start running fail";
        }
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @RequestMapping("/verifast/mipverifymock/{userId}")
    public String verifastUseMIPVerifyMock(@PathVariable String userId) {
        VerifastKit verifastKit = new VerifastKit();
        int status = verifastKit.testWithMIPVerifyMock(userId);
        if (status > 0) {
            return "start running successfully";
        } else {
            return "start running fail";
        }
    }

    @RequestMapping("/verifast/test")
    public String test() {
        return "socket_test";
    }
}
