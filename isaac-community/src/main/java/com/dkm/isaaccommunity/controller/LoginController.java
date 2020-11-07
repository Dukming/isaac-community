package com.dkm.isaaccommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.BaseUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/loginValidate", method = RequestMethod.POST)
    public JSONObject loginValidate(@RequestBody User user){
        return BaseUtil.getResult(userService.userPwdValidate(user.getPhone(), user.getPassword()));
    }


    @RequestMapping(value = "/getPage")
    public String getLoginPage(){
        return "login";
    }

    @RequestMapping(value = "/selectPage", method = RequestMethod.POST)
    public String selectPage(){
        User user = userService.getSessionUser();
        if(null == user){
            return "redirect:/login/getPage";
        }
        switch(user.getRole()){
            case 1:
                return "redirect:/adminItem/showAll";
            case 2:
                return "redirect:/item/showAll";
            default:
                return "redirect:/login/getPage";

        }
    }

    @RequestMapping(value = "/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login/getPage";
    }

}
