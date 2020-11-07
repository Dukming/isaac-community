package com.dkm.isaaccommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.bean.ErrorMessage;
import com.dkm.isaaccommunity.bean.MessagesList;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.service.ErrorMessageService;
import com.dkm.isaaccommunity.util.BaseUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/adminErrorMSG")
@RequiresRoles(value = {"1"})
public class ErrorAdminMSGController {

    @Autowired
    private ErrorMessageService errorMessageService;

    @RequestMapping(value = "/toErrorAdminMSG",method = RequestMethod.GET )
    public String toErrorAdminMSG(){
        return "adminErrorMessage/adminErrorMSG";
    }

    @ResponseBody
    @RequestMapping(value = "/getNotProcessedMSG", method = RequestMethod.GET)
    public MessagesList getNotProcessedMSG(@RequestParam("page")int page){
        return errorMessageService.getNotProcessedMSGByPage(page);
    }

    @ResponseBody
    @RequestMapping(value = "/getProcessedMSG", method = RequestMethod.GET)
    public MessagesList getProcessedMSG(@RequestParam("page")int page){
        return errorMessageService.getProcessedMSGByPage(page);
    }

    @ResponseBody
    @RequestMapping(value = "/signMSGProcessed", method = RequestMethod.POST)
    public JSONObject signMSGProcessed(@RequestBody ErrorMessage errorMessage){
        if(BaseUtil.isNullOrEmpty(String.valueOf(errorMessage.getId()))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(errorMessageService.sign(errorMessage.getId()));
    }

    @ResponseBody
    @RequestMapping(value = "/rewardScore", method = RequestMethod.POST)
    public JSONObject rewardScore(@RequestParam("userID") Integer userID){
        if(BaseUtil.isNullOrEmpty(String.valueOf(userID))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(errorMessageService.reward(userID));
    }

}
