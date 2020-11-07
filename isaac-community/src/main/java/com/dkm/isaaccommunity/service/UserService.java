package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.result.LoginResult;

public interface UserService {
    LoginResult userPwdValidate(String phone, String password);
    User getSessionUser();
    User getUser(Integer id);
    boolean saveHeadImg(String imgName);
    void saveRegisterUser(User user);
}
