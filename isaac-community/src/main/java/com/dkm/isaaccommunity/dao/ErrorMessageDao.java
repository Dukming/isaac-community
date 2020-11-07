package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.ErrorMessage;
import com.dkm.isaaccommunity.bean.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorMessageDao {
    int insertErrorMSG(ErrorMessage errorMessage);
    List<ErrorMessage> selectNotProcessedMSGByPage(@Param("pageInfo") PageInfo pageInfo);
    Integer selectNotProcessedMSGCount();
    List<ErrorMessage> selectProcessedMSGByPage(@Param("pageInfo") PageInfo pageInfo);
    Integer selectProcessedMSGCount();
    void signMessageToProcessed(@Param("id") Integer id);
}
