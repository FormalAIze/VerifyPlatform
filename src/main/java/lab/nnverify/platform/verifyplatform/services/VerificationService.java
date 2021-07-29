package lab.nnverify.platform.verifyplatform.services;

import com.alibaba.fastjson.JSON;
import lab.nnverify.platform.verifyplatform.mapper.VerificationMapper;
import lab.nnverify.platform.verifyplatform.models.AllParamsVerification;
import lab.nnverify.platform.verifyplatform.models.DeepCertVerification;
import lab.nnverify.platform.verifyplatform.models.WiNRVerification;
import lab.nnverify.platform.verifyplatform.verifykit.deepcert.DeepCertConfig;
import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VerificationService {
    @Autowired
    VerificationMapper verificationMapper;

    @Autowired
    DeepCertConfig deepCertConfig;

    @Autowired
    WiNRConfig wiNRConfig;

    public String saveTestImageInfo2Json(String verifyId, Map<String, String> testImageInfo, String tool) {
        HashMap<String, String> testImageInfoWithPath = new HashMap<>();
        if (tool.equalsIgnoreCase("winr")) {
            for (String filename : testImageInfo.keySet()) {
                testImageInfoWithPath.put(wiNRConfig.getUploadImageFilepath() + filename, testImageInfo.get(filename));
            }
            return saveTestImageInfo2JsonInner(verifyId, testImageInfoWithPath);
        } else if (tool.equalsIgnoreCase("deepcert")) {
            for (String filename : testImageInfo.keySet()) {
                testImageInfoWithPath.put(deepCertConfig.getUploadImageFilepath() + filename, testImageInfo.get(filename));
            }
            return saveTestImageInfo2JsonInner(verifyId, testImageInfoWithPath);
        }
        return "";
    }

    private String saveTestImageInfo2JsonInner(String verifyId, Map<String, String> testImageInfo) {
        String json = JSON.toJSONString(testImageInfo);
        String jsonFilepath = wiNRConfig.getJsonPath() + verifyId;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(jsonFilepath));
            out.write(json);
            out.close();
            log.info("write json file success, filepath: " + jsonFilepath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("write json file failed, filepath: " + jsonFilepath);
        }
        return jsonFilepath;
    }

    public int saveTestImageOfVerification(String verifyId, Map<String, String> testImageInfo) {
        int successCount = 0;
        for (String filename : testImageInfo.keySet()) {
            String label = testImageInfo.get(filename);
            successCount += verificationMapper.saveTestImageOfVerification(verifyId, filename, label);
        }
        return successCount;
    }

    public List<AllParamsVerification> findVerificationHistoryByUserId(Integer userId) {
        List<AllParamsVerification> verifications = verificationMapper.fetchVerificationByUserId(userId);
        // 修改为东8区 不知道为什么数据库显示的时间是东8区时间 但是程序获取到的时间是UTC时间 手动加8小时
        for (AllParamsVerification verification : verifications) {
            verification.setStartTime(Timestamp.valueOf(verification.getStartTime().toLocalDateTime().plusHours(8)));
        }
        return verifications;
    }

    public WiNRVerification fetchWiNRVerificationById(String verifyId) {
        return verificationMapper.fetchWiNRVerificationById(verifyId);
    }

    public DeepCertVerification fetchDeepCertVerificationById(String verifyId) {
        return verificationMapper.fetchDeepCertVerificationById(verifyId);
    }

    public String fetchVerificationTool(String verifyId) {
        return verificationMapper.fetchVerificationToolById(verifyId);
    }

    public boolean saveWiNRVerificationParams(WiNRVerification params) {
        int modified = verificationMapper.insertWiNRVerificationRecord(params);
        return modified != 0;
    }

    public boolean finishVerificationUpdateStatus(String verifyId, String status) {
        int modified = verificationMapper.updateVerificationRecordStatus(verifyId, status);
        return modified != 0;
    }

    public boolean saveDeepCertVerificationParams(DeepCertVerification params) {
        int modified = verificationMapper.insertDeepCertVerificationRecord(params);
        return modified != 0;
    }
}
