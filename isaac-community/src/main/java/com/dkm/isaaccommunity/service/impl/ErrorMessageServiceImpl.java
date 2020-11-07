package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.ErrorMessage;
import com.dkm.isaaccommunity.bean.MessagesList;
import com.dkm.isaaccommunity.bean.PageInfo;
import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.dao.ErrorMessageDao;
import com.dkm.isaaccommunity.dao.UserDao;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.result.IResult;
import com.dkm.isaaccommunity.service.ErrorMessageService;
import com.dkm.isaaccommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErrorMessageServiceImpl implements ErrorMessageService {

    @Autowired
    private ErrorMessageDao errorMessageDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Override
    public boolean sendMSG(String content) {
        User user = userService.getSessionUser();
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setCreateTime(LocalDateTime.now());
        errorMessage.setUser(user);
        errorMessage.setContent(content);
        if(errorMessageDao.insertErrorMSG(errorMessage) <= 0){
            return false;
        }
        return true;
    }

    @Override
    public MessagesList getNotProcessedMSGByPage(int page) {
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.QUESTIONS_NUM);
        List<ErrorMessage> errorMessages = errorMessageDao.selectNotProcessedMSGByPage(pageInfo);
        Long sum = errorMessageDao.selectNotProcessedMSGCount().longValue();
        for(int i = 0, len = errorMessages.size(); i < len; i++){
            String headImgName = errorMessages.get(i).getUser().getImgName();
            errorMessages.get(i).getUser().setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + headImgName);
        }
        return new MessagesList(errorMessages, sum);
    }

    @Override
    public MessagesList getProcessedMSGByPage(int page) {
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.QUESTIONS_NUM);
        List<ErrorMessage> errorMessages = errorMessageDao.selectProcessedMSGByPage(pageInfo);
        Long sum = errorMessageDao.selectProcessedMSGCount().longValue();
        for(int i = 0, len = errorMessages.size(); i < len; i++){
            String headImgName = errorMessages.get(i).getUser().getImgName();
            errorMessages.get(i).getUser().setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + headImgName);
        }
        return new MessagesList(errorMessages, sum);
    }

    @Override
    public IResult sign(int msgID) {
        errorMessageDao.signMessageToProcessed(msgID);
        return BaseResult.SUCCESS;
    }

    @Override
    public IResult reward(int userID) {
        userDao.increaseUserScore(userID,GlobalConstant.ERROR_MSG_REWARDSCORE);
        return BaseResult.SUCCESS;
    }
}
