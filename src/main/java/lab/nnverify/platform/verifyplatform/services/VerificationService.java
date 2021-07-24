package lab.nnverify.platform.verifyplatform.services;

import lab.nnverify.platform.verifyplatform.mapper.VerificationMapper;
import lab.nnverify.platform.verifyplatform.models.AllParamsVerification;
import lab.nnverify.platform.verifyplatform.models.DeepCertVerification;
import lab.nnverify.platform.verifyplatform.models.WiNRVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificationService {
    @Autowired
    VerificationMapper verificationMapper;

    public List<AllParamsVerification> findVerificationHistoryByUserId(Integer userId) {
        return verificationMapper.fetchVerificationByUserId(userId);
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
