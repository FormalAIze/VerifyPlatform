package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.verifykit.verifast.VerifastKit;
import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class WiNRController {
    WiNRKit wiNRKit = new WiNRKit();

    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/winr/sync/{userId}")
    public Map<String, Object> WiNRVerifySync(@PathVariable String userId, @RequestParam Map<String, Object> params) throws IOException {
        // todo verifyId还没想好怎么搞
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        int verifyId = 1;
        int status = wiNRKit.testSync(userId);
        List<String> resultFile = wiNRKit.sendResultFileSync();

        HashMap<String, Object> result = new HashMap<>();
        result.put("verifyId", verifyId);
        if (status > 0) {
            result.put("status", "start running successfully");
            result.put("resultFile", resultFile);
        } else {
            result.put("status", "start running fail");
        }
        List<String> advExamples = wiNRKit.sendAdvExample(verifyId);
        result.put("advExamples", advExamples);
        return result;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @RequestMapping("/winr/adv/{verifyId}")
    public List<String> getAdvExample(@PathVariable String verifyId) {
        return wiNRKit.sendAdvExample(Integer.parseInt(verifyId));
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/winr/mock/{userId}")
    public Map<String, Object> WiNRVerifyMock(@PathVariable String userId, @RequestParam Map<String, Object> params) throws IOException {
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        int verifyId = 1;
        int status = wiNRKit.testMockSync(userId);
        List<String> resultFile = wiNRKit.sendResultFileSync();

        HashMap<String, Object> result = new HashMap<>();
        result.put("verifyId", verifyId);
        if (status > 0) {
            result.put("status", "start running successfully");
            result.put("resultFile", resultFile);
        } else {
            result.put("status", "start running fail");
        }
        List<String> advExamples = wiNRKit.sendAdvExample(verifyId);
        result.put("advExamples", advExamples);
        return result;
    }
}
