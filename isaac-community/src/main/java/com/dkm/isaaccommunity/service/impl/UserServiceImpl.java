package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.dao.UserDao;
import com.dkm.isaaccommunity.result.LoginResult;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.BaseUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public LoginResult userPwdValidate(String phone, String password) {
        if(BaseUtil.isNullOrEmpty(phone) || BaseUtil.isNullOrEmpty(password)){
            return LoginResult.NULL_NAME_PWD;
        }

        //获取验证token
        UsernamePasswordToken token = new UsernamePasswordToken(phone, password);

        //用户名、密码验证
        Subject subject = SecurityUtils.getSubject();

        //登录验证，异常由异常处理对象来处理
        subject.login(token);

        //保存用户到session
        //User user = userDao.getUserByPhone(phone);
        subject.getSession().setAttribute(GlobalConstant.SESSION_USER_NAME, phone);

        return LoginResult.SUCCESS;
    }

    @Override
    @Cacheable(value = "redisCacheManager")
    public User getSessionUser() {
        Subject subject = SecurityUtils.getSubject();
        String phone = (String) subject.getSession().getAttribute(GlobalConstant.SESSION_USER_NAME);
        User user = userDao.getUserByPhone(phone);
        user.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + user.getImgName());
        return user;
    }

    @Override
    public User getUser(Integer id) {
        return userDao.getUser(id);
    }

    @Override
    public boolean saveHeadImg(String imgName) {
        User user = getSessionUser();
        if(userDao.updateHeadImg(imgName, user.getId()) <= 0){
            return false;
        }
        return true;
    }

    @Override
    public void saveRegisterUser(User user) {
        //用户密码加盐
        String salt = UUID.randomUUID().toString().replaceAll("-","");

        //加密密码
        SimpleHash simpleHash = new SimpleHash(GlobalConstant.PWD_MD5,
                user.getPassword(), salt, GlobalConstant.PASSWORD_ENCRYPT_COUNT);

        user.setSalt(salt);
        user.setCreateTime(LocalDateTime.now());
        String newPW = simpleHash.toHex();
        userDao.insertUser(user, newPW);
    }


}
