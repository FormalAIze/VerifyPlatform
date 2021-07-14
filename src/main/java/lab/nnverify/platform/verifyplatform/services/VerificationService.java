package lab.nnverify.platform.verifyplatform.services;

import lab.nnverify.platform.verifyplatform.mapper.VerificationMapper;
import lab.nnverify.platform.verifyplatform.models.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VerificationService {
    @Autowired
    VerificationMapper verificationMapper;

    public List<Verification> findVerificationHistoryByUserId(Integer userId) {
        return verificationMapper.fetchVerificationByUserId(userId);
    }

    public Verification fetchVerificationById(String verifyId) {
        return verificationMapper.fetchVerificationById(verifyId);
    }

    public boolean saveVerificationParams(Verification params) {
        int modified = verificationMapper.insertVerificationRecord(params);
        return modified != 0;
    }

    public boolean finishVerificationUpdateStatus(String verifyId, String status) {
        int modified = verificationMapper.updateVerificationRecordStatus(verifyId, status);
        return modified != 0;
    }
}
