package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.MessagesList;
import com.dkm.isaaccommunity.result.IResult;

public interface ErrorMessageService {
    boolean sendMSG(String content);
    MessagesList getNotProcessedMSGByPage(int page);
    MessagesList getProcessedMSGByPage(int page);
    IResult sign(int msgID);
    IResult reward(int userID);
}
