package com.dkm.isaaccommunity.controller;

import com.dkm.isaaccommunity.bean.Article;
import com.dkm.isaaccommunity.service.ErrorMessageService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/errorMessage")
@RequiresRoles(value = {"2"})
public class ErrorMessageController {

    @Autowired
    private ErrorMessageService errorMessageService;

    @RequestMapping(value = "/toSendMSG",method = RequestMethod.GET )
    public String toSendMSG(){
        return "errorMessage/sendErrorMessage";
    }

    @ResponseBody
    @RequestMapping(value = "/sendMSG",method = RequestMethod.POST )
    public String sendMSG(@RequestParam("content") String content){
        if(errorMessageService.sendMSG(content)){
            return "<script>alert('send message success!');window.location.href='/errorMessage/toSendMSG';</script>";
        }else{
            return "<script>alert('send message failed!!');window.location.href='/errorMessage/toSendMSG';</script>";
        }
    }


}
