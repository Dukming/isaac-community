package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.PageInfo;
import com.dkm.isaaccommunity.bean.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionDao {
    int insertQuestion(@Param("question") Question question);
    void insertAttentionQuestion(@Param("qid") Integer qid, @Param("uid") Integer uid, @Param("createTime") LocalDateTime createTime);
    void deleteAttentionQuestion(@Param("qid") Integer qid, @Param("uid") Integer uid);
    List<Question> getQuestionsList(@Param("qids") String qids);
    Question selectQuestionById(@Param("qid") Integer qid);
    List<Question> getUserQuestions(@Param("pageInfo") PageInfo pageInfo);
    Integer selectUserQuestionCount(@Param("userID") Integer userID);
    Integer selectQuestionAnswerCount(@Param("qid") Integer qid);
    List<Question> getUserAttentions(@Param("pageInfo") PageInfo pageInfo);
    Integer selectUserAttentionCount(@Param("userID") Integer userID);
    Question selectUserTheNewestQuestion(@Param("userID") Integer userID);
}
