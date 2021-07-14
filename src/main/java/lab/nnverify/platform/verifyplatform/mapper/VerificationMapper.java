package lab.nnverify.platform.verifyplatform.mapper;

import lab.nnverify.platform.verifyplatform.models.Verification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface VerificationMapper {
    @Select("select verify_id,user_id,tool,epsilon,dataset,num_of_image,net_name,status from verification where user_id=#{userId}")
    List<Verification> fetchVerificationByUserId(Integer userId);

    @Insert("insert into verification(verify_id,user_id,tool,epsilon,dataset,num_of_image,net_name,status) values " +
            "(#{verifyId},#{userId},#{tool},#{epsilon},#{dataset},#{numOfImage},#{netName},#{status})")
    int insertVerificationRecord(Verification params);

    @Update("update verification set status=#{status} where verify_id=#{verifyId}")
    int updateVerificationRecordStatus(String verifyId, String status);
}
