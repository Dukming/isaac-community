package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.result.IResult;

public interface RegisterService {
    IResult registerValidate(User user, String captchaId);
}
