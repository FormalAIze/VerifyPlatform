package lab.nnverify.platform.verifyplatform.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 这是用来获取数据库verification表查询数据的model类
 * 是两种工具的参数并集，因为数据库里都是一张表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllParamsVerification {
    private String verifyId;
    private int userId;
    private String tool;

    private String netName;
    private String numOfImage;
    private String norm;
    private String core;
    private String activation;
    private String isCifar;
    private String isTinyImageNet;

    private String epsilon;
    private String dataset;

    // 四种状态: ready, running, success, error
    private String status;
    private Timestamp startTime;
}
