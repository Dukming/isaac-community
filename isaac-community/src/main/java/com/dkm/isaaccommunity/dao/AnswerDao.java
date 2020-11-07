package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.bean.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnswerDao {
    int insertAnswer(@Param("answer") Answer answer);
    void insertCollectAnswer(@Param("aid") Integer aid, @Param("uid") Integer uid, @Param("createTime")LocalDateTime createTime);
    void deleteCollectAnswer(@Param("aid") Integer aid, @Param("uid") Integer uid);
    List<Answer> getQuestionAllAnswers(@Param("pageInfo") PageInfo pageInfo);
    Answer getAnswer(@Param("aid") Integer aid);
    List<Answer> getUserAnswers(@Param("pageInfo") PageInfo pageInfo);
    Integer selectUserAnswerCount(@Param("userID") Integer userID);
    void increaseStarsNum(@Param("aid") Integer aid);
    void decreaseStarsNum(@Param("aid") Integer aid);
    void updateAnswerContent(@Param("aid") Integer aid, @Param("content") String content);
    List<Answer> getUserCollections(@Param("pageInfo") PageInfo pageInfo);
    Integer selectUserCollectionCount(@Param("userID") Integer userID);
    Answer selectUserTheNewestAnswer(@Param("userID") Integer userID);
}
