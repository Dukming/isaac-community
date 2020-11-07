package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.bean.AnswersList;
import com.dkm.isaaccommunity.bean.QuestionsList;
import com.dkm.isaaccommunity.result.IResult;

public interface AnswerService {
    IResult addAnswer(Answer answer);
    IResult editAnswer(Answer answer);
    IResult collectAnswer(Integer aid);
    IResult cancelCollectAnswer(Integer aid);
    AnswersList getQuestionAllAnswers(int page, int qid);
    QuestionsList getUserAnswers(int page);
    Long starAction(Integer aid , Integer qid);
    QuestionsList getUserCollections(int page);
}
