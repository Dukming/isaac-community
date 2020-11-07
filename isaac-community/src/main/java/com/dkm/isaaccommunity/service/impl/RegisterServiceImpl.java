package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.result.IResult;
import com.dkm.isaaccommunity.result.RegisterResult;
import com.dkm.isaaccommunity.service.RedisService;
import com.dkm.isaaccommunity.service.RegisterService;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.BaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @Override
    public IResult registerValidate(User user, String captchaId) {
        //验证用户名格式
        if(!BaseUtil.validateUserName(user.getUserName())){
            return RegisterResult.INVALIDATE_USERNAME;
        }

        //验证密码格式
        if(!BaseUtil.validatePassword(user.getPassword())){
            return RegisterResult.INVALIDATE_PASSWORD;
        }

        //验证手机号格式
        if(!BaseUtil.isPhone(user.getPhone())){
            return RegisterResult.INVALIDATE_PHONE;
        }

        //验证手机号是否已注册过
        if(redisService.validatePhoneHasRegister(user.getPhone())){
            return RegisterResult.PHONE_HAS_REGISTER;
        }

        //验证验证码
        if(!captchaId.equals(user.getVercode())){
            return RegisterResult.INVALIDATE_VERCODE;
        }

        try {
            //保存注册用户信息
            userService.saveRegisterUser(user);
            //验证成功缓存已注册手机号
            redisService.saveHasRegisterPhone(user);
        }catch (Exception e){
            e.printStackTrace();
            return RegisterResult.REGISTER_FAILURE;
        }

        return RegisterResult.SUCCESS;
    }
}
