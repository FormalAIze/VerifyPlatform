package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@Scope("prototype")
public class WiNRController {
    WiNRKit wiNRKit = new WiNRKit();

    @GetMapping("/winr/verify")
    public String WiNRVerify() {
        return "WiNR_test";
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @GetMapping("/winr/verify_id")
    public String initVerifyId() {
        String verifyId = UUID.randomUUID().toString().replace("-", "");
        log.info("verifyId is: " + verifyId);
        return verifyId;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @GetMapping("/winr/verification/{verifyId}")
    public Map<String, Object> fetchVerificationResult(@PathVariable String verifyId) throws IOException {
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("verifyId", verifyId);
        wiNRKit.setParams(params);
        Map<String, String> resultFile = wiNRKit.getResultSync();
        log.info(resultFile.toString());
        // 可能会出现的异常情况：没有成功运行验证但是需要获取结果
        result.put("resultFile", resultFile);
        int image_num = Integer.parseInt(resultFile.get("unrobust_number")) * 2;
        List<String> advExamples = wiNRKit.getAdvExample(image_num);
        result.put("advExamples", advExamples);
        result.put("verifyId", verifyId);
        return result;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/winr/sync/{userId}")
    public Map<String, Object> verifySync(@PathVariable String userId, @RequestParam Map<String, Object> params) throws IOException {
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        HashMap<String, Object> result = new HashMap<>();
        String verifyId = (String) params.get("verifyId");
        if (verifyId == null) {
            result.put("status", "no verify id provided");
            return result;
        }
        wiNRKit.setParams(params);
        int status = wiNRKit.testSync(userId);
        Map<String, String> resultFile = wiNRKit.getResultSync();
        log.info(resultFile.toString());
        if (status > 0) {
            result.put("status", "start running successfully");
            result.put("resultFile", resultFile);
        } else {
            result.put("status", "start running fail");
        }
        int image_num = Integer.parseInt(resultFile.get("unrobust_number")) * 2;
        List<String> advExamples = wiNRKit.getAdvExample(image_num);
        result.put("advExamples", advExamples);
        result.put("verifyId", verifyId);
        return result;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @RequestMapping("/winr/adv/{verifyId}")
    public List<String> getAdvExample(@PathVariable String verifyId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("verifyId", verifyId);
        wiNRKit.setParams(params);
        return wiNRKit.getAdvExample(1);
    }

    @Deprecated
    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/winr/mock/{userId}")
    public Map<String, Object> WiNRVerifyMock(@PathVariable String userId, @RequestParam Map<String, Object> params) throws IOException {
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        String verifyId = "1";
        int status = wiNRKit.testMockSync(userId);
        Map<String, String> resultFile = wiNRKit.getResultSync();

        HashMap<String, Object> result = new HashMap<>();
        result.put("verifyId", verifyId);
        if (status > 0) {
            result.put("status", "start running successfully");
            result.put("resultFile", resultFile);
        } else {
            result.put("status", "start running fail");
        }
        int image_num = Integer.parseInt(resultFile.get("unrobust_number")) * 2;
        List<String> advExamples = wiNRKit.getAdvExample(image_num);
        result.put("advExamples", advExamples);
        return result;
    }
}
