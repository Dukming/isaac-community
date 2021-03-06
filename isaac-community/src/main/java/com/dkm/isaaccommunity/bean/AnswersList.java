package com.dkm.isaaccommunity.bean;

import java.io.Serializable;
import java.util.List;

public class AnswersList implements Serializable {
    private static final long serialVersionUID = 1L;

    //问题所有回答集合（当前查询页）
    private List<Answer> answersList;
    //回答总数量
    private Long sum;


    public AnswersList(List<Answer> answersList, Long sum) {
        this.answersList = answersList;
        this.sum = sum;
    }

    public List<Answer> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(List<Answer> answersList) {
        this.answersList = answersList;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }
}
