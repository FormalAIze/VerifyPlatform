package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.DeepCertVerification;
import lab.nnverify.platform.verifyplatform.models.ResponseEntity;
import lab.nnverify.platform.verifyplatform.models.WiNRVerification;
import lab.nnverify.platform.verifyplatform.services.VerificationService;
import lab.nnverify.platform.verifyplatform.verifykit.deepcert.DeepCertKit;
import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class VerificationController {
    @Autowired
    WiNRKit wiNRKit;

    @Autowired
    DeepCertKit deepCertKit;

    @Autowired
    VerificationService verificationService;

    @GetMapping("/winr")
    public String WiNRVerify() {
        return "WiNR_test";
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @GetMapping("/verify/verify_id")
    public ResponseEntity initVerifyId() {
        String verifyId = UUID.randomUUID().toString().replace("-", "");
        log.info("verifyId is: " + verifyId);
        ResponseEntity response = new ResponseEntity();
        response.setStatus(200);
        response.getData().put("verifyId", verifyId);
        return response;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @GetMapping("/verify/verification")
    public ResponseEntity fetchVerificationResult(@RequestParam String verifyId) throws IOException {
        String tool = verificationService.fetchVerificationTool(verifyId);
        Map<String, String> result;
        ResponseEntity response = new ResponseEntity();
        switch (tool) {
            case "WiNR": {
                WiNRVerification verification = verificationService.fetchWiNRVerificationById(verifyId);
                wiNRKit.setParams(verification);
                result = wiNRKit.getResultSync();
                log.info(result.toString());
                if (verification.getStatus().equals("success")) {
                    response.setStatus(200);
                    response.setMsg("verification successfully end");
                    response.getData().put("result", result);
                    int image_num = Integer.parseInt(result.get("unrobust_number")) * 2;
                    List<String> advExamples = wiNRKit.getAdvExample(image_num);
                    response.getData().put("advExamples", advExamples);
                } else {
                    response.setStatus(-500);
                    response.setMsg("verification failed");
                    response.getData().put("verificationStatus", verification.getStatus());
                }
                response.getData().put("verifyId", verifyId);
                break;
            }
            case "DeepCert": {
                DeepCertVerification verification = verificationService.fetchDeepCertVerificationById(verifyId);
                deepCertKit.setParams(verification);
                result = deepCertKit.getResultSync();
                log.info(result.toString());
                if (verification.getStatus().equals("success")) {
                    response.setStatus(200);
                    response.setMsg("verification successfully end");
                    response.getData().put("result", result);
                } else {
                    response.setStatus(-500);
                    response.setMsg("verification failed");
                    response.getData().put("verificationStatus", verification.getStatus());
                }
                response.getData().put("verifyId", verifyId);
                break;
            }
        }
        return response;
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/verify/deepcert/{userId}")
    public ResponseEntity verifyAsyncDeepCert(@PathVariable Integer userId, @RequestParam Map<String, Object> params) {
        ResponseEntity response = new ResponseEntity();
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        String verifyId = (String) params.get("verifyId");
        if (verifyId == null || verifyId.isBlank()) {
            response.setStatus(410);
            response.setMsg("no verify id provided");
            return response;
        }
        // todo winr那边ok了之后 这边搞个一样的
        DeepCertVerification verificationParams = new DeepCertVerification(verifyId, userId, "DeepCert", (String) params.get("netName"),
                (String) params.get("numOfImage"), (String) params.get("norm"), (String) params.get("core"), (String) params.get("activation"),
                (String) params.get("isCifar"), (String) params.get("isTinyImageNet"), "ready", getNowTimestamp());
        deepCertKit.setParams(verificationParams);
        int status = deepCertKit.testAsync();
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
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @PostMapping("/verify/winr/{userId}")
    public ResponseEntity verifyAsyncWiNR(@PathVariable Integer userId, @RequestParam Map<String, Object> params) throws IOException {
        ResponseEntity response = new ResponseEntity();
        for (String key : params.keySet()) {
            log.info(key + ": " + params.get(key).toString());
        }
        String verifyId = (String) params.get("verifyId");
        if (verifyId == null || verifyId.isBlank()) {
            response.setStatus(410);
            response.setMsg("no verify id provided");
            return response;
        }
        // todo 验证图片和model是否存在
        // 保存用户上传的验证图片信息到数据库
        Map<String, String> testImageInfo = (Map<String, String>) params.get("testImageInfo");
        String imageInfoJsonFile = verificationService.saveTestImageInfo2Json(verifyId, testImageInfo, "WiNR");
        if (imageInfoJsonFile.isBlank()) {
            response.setStatus(420);
            response.setMsg("image info json file save fail");
        }
        int successSaveCount = verificationService.saveTestImageOfVerification(verifyId, testImageInfo);
        log.info("verification test images saved. " + successSaveCount + "/" + testImageInfo.keySet().size());
        // todo model信息也需要存到数据库，可能也需要修改
        // todo 需要修改以配合工具
        WiNRVerification verificationParams = new WiNRVerification(verifyId, userId, "WiNR", (String) params.get("epsilon"),
                (String) params.get("model"), (String) params.get("dataset"), (String) params.get("imageNum"),
                "ready", getNowTimestamp());
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
    }

    private Timestamp getNowTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return Timestamp.valueOf(simpleDateFormat.format(new Date()));
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
}
