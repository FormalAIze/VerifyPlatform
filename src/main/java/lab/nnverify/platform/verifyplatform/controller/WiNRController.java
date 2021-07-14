package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.ResponseEntity;
import lab.nnverify.platform.verifyplatform.models.Verification;
import lab.nnverify.platform.verifyplatform.services.VerificationService;
import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
public class WiNRController {
    @Autowired
    WiNRKit wiNRKit;

    @Autowired
    VerificationService verificationService;

    @GetMapping("/winr")
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
    @GetMapping("/winr/verification")
    public ResponseEntity fetchVerificationResult(@RequestParam String verifyId) throws IOException {
        Verification verification = verificationService.fetchVerificationById(verifyId);
        ResponseEntity response = new ResponseEntity();
        wiNRKit.setParams(verification);
        Map<String, String> resultFile = wiNRKit.getResultSync();
        log.info(resultFile.toString());
        if (verification.getStatus().equals("successfully end")) {
            response.setMsg("verification successfully end");
            response.getData().put("resultFile", resultFile);
            int image_num = Integer.parseInt(resultFile.get("unrobust_number")) * 2;
            List<String> advExamples = wiNRKit.getAdvExample(image_num);
            response.getData().put("advExamples", advExamples);
        } else {
            response.setMsg("verification fail");
        }
        response.getData().put("verifyId", verifyId);
        return response;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/winr/verify/{userId}")
    public ResponseEntity verifyAsync(@PathVariable Integer userId, @RequestParam Map<String, Object> params) throws IOException {
        ResponseEntity response = new ResponseEntity();
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        String verifyId = (String) params.get("verifyId");
        if (verifyId == null) {
            response.setStatus(410);
            response.setMsg("no verify id provided");
            return response;
        }
        Verification verificationParams = new Verification(verifyId, userId, "WiNR", (String) params.get("epsilon"),
                (String) params.get("model"), (String) params.get("dataset"), (String) params.get("imageNum"),
                "ready");
        wiNRKit.setParams(verificationParams);
        int status = wiNRKit.testAsync();
        if (status == -100) {
            log.error("no web socket session for verify: " + verificationParams.getVerifyId());
            response.setMsg("no web socket session for verify: " + verificationParams.getVerifyId());
            response.setStatus(-100);
        } else if (status == -400) {
            log.error("params check not passed for verify: " + verificationParams.getVerifyId());
            response.setMsg("params check not passed for verify: " + verificationParams.getVerifyId());
            response.setStatus(-400);
        } else if (status == 1) {
            log.info("async verification thread start successfully, verifyId: " + verificationParams.getVerifyId());
            response.setMsg("async verification thread start successfully, verifyId: " + verificationParams.getVerifyId());
            response.setStatus(200);
        }
        return response;
//        if (wiNRKit.getRunStatus() != 0) {
//            response.setStatus(510);
//            response.setMsg("verification task end with error");
//            return response;
//        }
//        Map<String, String> resultFile = wiNRKit.getResultSync();
//        log.info(resultFile.toString());
//        if (status > 0) {
//            response.setMsg("start running successfully");
//            response.getData().put("resultFile", resultFile);
//        } else {
//            response.setMsg("start running fail");
//        }
//        int image_num = Integer.parseInt(resultFile.get("unrobust_number")) * 2;
//        List<String> advExamples = wiNRKit.getAdvExample(image_num);
//        response.getData().put("advExamples", advExamples);
//        response.getData().put("verifyId", verifyId);
    }

//    @ResponseBody
//    @CrossOrigin(origins = "*")
//    @RequestMapping("/winr/adv/{verifyId}")
//    public ResponseEntity getAdvExample(@PathVariable String verifyId) {
//        ResponseEntity response = new ResponseEntity();
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("verifyId", verifyId);
//        wiNRKit.setParams(params);
//        response.setStatus(200);
//        response.getData().put("advExamples", wiNRKit.getAdvExample(1));
//        return response;
//    }

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
