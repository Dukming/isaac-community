package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.bean.QuestionsList;
import com.dkm.isaaccommunity.result.IResult;

import java.util.List;

public interface QuestionService {
    IResult addQuestion(Question question);
    IResult attentionQuestion(Integer qid);
    IResult cancelAttentionQuestion(Integer qid);
    QuestionsList getNewestQuestionsDetails(int page, long time);
    List<Question> getNewestQuestions();
    Question getQuestion(int qid);
    Question getQuestionAndAnswer(int qid, int aid);
    QuestionsList getHotQuestionsByPage(int page, String token);
    QuestionsList getUserQuestions(int page);
    QuestionsList getUserAttentions(int page);
}
