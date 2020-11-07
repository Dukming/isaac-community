package com.dkm.isaaccommunity.bean;

import java.io.Serializable;
import java.util.List;

public class MessagesList implements Serializable {
    private static final long serialVersionUID = 1L;

    //信息列表
    private List<ErrorMessage> msgList;
    //信息总数
    private Long sum;

    public MessagesList(List<ErrorMessage> msgList, Long sum) {
        this.msgList = msgList;
        this.sum = sum;
    }


    public List<ErrorMessage> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<ErrorMessage> msgList) {
        this.msgList = msgList;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }
}
