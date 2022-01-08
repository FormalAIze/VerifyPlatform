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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class VerificationService {
    @Autowired
    VerificationMapper verificationMapper;

    @Autowired
    DeepCertConfig deepCertConfig;

    @Autowired
    WiNRConfig wiNRConfig;

    public boolean isModelAndTestImageExist(String model, Set<String> testImages, String tool) {
        String modelPath;
        if (tool.equalsIgnoreCase("winr")) {
            modelPath = wiNRConfig.getUploadModelFilepath() + model;
        } else {
            modelPath = deepCertConfig.getUploadModelFilepath() + model;
        }
        log.info("modelPath: " + modelPath);
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            return false;
        }
        for (String testImage : testImages) {
            String testImagePath;
            if (tool.equalsIgnoreCase("winr")) {
                testImagePath = wiNRConfig.getUploadImageFilepath() + testImage;
            } else {
                testImagePath = deepCertConfig.getUploadImageFilepath() + testImage;
            }
            log.info("testImagePath: " + testImagePath);
            File file = new File(testImagePath);
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    public boolean paramsCheckWiNR(WiNRVerification verification) {
        String dataset = verification.getDataset();
        String epsilon = verification.getEpsilon();
        String model = verification.getNetName();
        Map<String, String> testImageInfo = verification.getTestImageInfo();
        String pureConv = verification.getPureConv();
        return !dataset.isBlank() &&
                !epsilon.isBlank() &&
                !model.isBlank() &&
                !(testImageInfo == null || testImageInfo.keySet().size() == 0) &&
                !pureConv.isBlank();
    }

    public boolean paramsCheckDeepcert(DeepCertVerification verification) {
        String netName = verification.getNetName();
        Map<String, String> testImageInfo = verification.getTestImageInfo();
        String norm = verification.getNorm();
        return !netName.isBlank() &&
                !(testImageInfo == null || testImageInfo.keySet().size() == 0) &&
                !norm.isBlank();
    }

    public String saveTestImageInfo2Json(String verifyId, Map<String, String> testImageInfo, String tool) {
        if (tool.equalsIgnoreCase("winr")) {
            HashMap<String, Map<String, Object>> testImageInfoWithPath = new HashMap<>();
            // convert to a json format that winr tool accept
            int i = 0;
            for (String filename : testImageInfo.keySet()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("path", wiNRConfig.getUploadImageFilepath() + filename);
                map.put("label", Integer.valueOf(testImageInfo.get(filename)));
                testImageInfoWithPath.put("img_" + i++, map);
            }
            String json = JSON.toJSONString(testImageInfoWithPath) + "\n";
            String jsonFilepath = wiNRConfig.getJsonPath() + verifyId + ".json";
            return saveTestImageInfo2JsonInner(json, jsonFilepath);
        } else if (tool.equalsIgnoreCase("deepcert")) {
            HashMap<String, Integer> testImageInfoWithPath = new HashMap<>();
            int i = 0;
            // save file to another directory and rename
            for (String filename : testImageInfo.keySet()) {
                // copy file
                String filepath = deepCertConfig.getUploadImageFilepath() + filename;
                String label = testImageInfo.get(filename);
                int idx = filename.lastIndexOf(".");
                String extension = filename.substring(idx);
                String dest = deepCertConfig.getOriginImageBasePath() + verifyId + "/image_" + i + extension;
                File originFile = new File(filepath);
                File destFile = new File(dest);
                if (!destFile.getParentFile().exists()) {
                    if (!destFile.getParentFile().mkdirs()) {
                        log.error("mkdirs files, path: " + destFile);
                    }
                }
                try {
                    Files.copy(originFile.toPath(), destFile.toPath());
                    testImageInfoWithPath.put(dest, Integer.valueOf(label));
                    i++;
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("file copy failed: " + originFile + " -> " + destFile);
                }
            }
            String json = JSON.toJSONString(testImageInfoWithPath) + "\n";
            String jsonFilepath = deepCertConfig.getJsonPath() + verifyId + ".json";
            if (i == 0) { // all files fail to copy to dest path
                return "";
            } else {
                return saveTestImageInfo2JsonInner(json, jsonFilepath);
            }
        }
        return "";
    }

    private String saveTestImageInfo2JsonInner(String json, String jsonFilepath) {
        File jsonFile = new File(jsonFilepath);
        if (!jsonFile.getParentFile().exists()) {
            if (!jsonFile.getParentFile().mkdirs()) {
                log.error("mkdirs files, path: " + jsonFilepath);
            }
        }
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

    // 与验证工具无关
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
//        // 修改为东8区 不知道为什么数据库显示的时间是东8区时间 但是程序获取到的时间是UTC时间 手动加8小时
//        for (AllParamsVerification verification : verifications) {
//            verification.setStartTime(Timestamp.valueOf(verification.getStartTime().toLocalDateTime().plusHours(8)));
//        }
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
