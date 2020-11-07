package com.dkm.isaaccommunity.exception;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.result.IResult;
import com.dkm.isaaccommunity.result.LoginResult;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(basePackages = "com.dkm.isaaccommunity")
public class GlobalExceptionHandler {

    //密码不正确
    @ExceptionHandler(IncorrectCredentialsException.class)
    public ModelAndView incorrectCredentialsExceptionHandler(HttpServletResponse response){
        //异常返回结果
        IResult iResult = LoginResult.INCORRECT_PWD;
        //返回异常结果JSON数据
        JSONObject result = new JSONObject();
        result.put("code", iResult.getCode());
        result.put("message", iResult.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");

        try {
            response.getWriter().write(result.toJSONString());
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }

        return new ModelAndView();
    }


    //用户不存在
    @ExceptionHandler(UnknownAccountException.class)
    public ModelAndView unknownAccountExceptionHandler(HttpServletResponse response){
        //异常返回结果
        IResult iResult = LoginResult.NO_ACCOUNT;
        //返回异常结果JSON数据
        JSONObject result = new JSONObject();
        result.put("code", iResult.getCode());
        result.put("message", iResult.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");

        try {
            response.getWriter().write(result.toJSONString());
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }

        return new ModelAndView();
    }


    //权限不足
    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorizedExceptionHandler(){
        return "<script>alert('Your privileges are insufficient！');history.go(-1);</script>";
    }

    //账号被锁定
    @ExceptionHandler(LockedAccountException.class)
    public ModelAndView lockedAccountExceptionHandler(HttpServletResponse response){
        //异常返回结果
        IResult iResult = LoginResult.LOCKED_ACCOUNT;
        //返回异常结果JSON数据
        JSONObject result = new JSONObject();
        result.put("code", iResult.getCode());
        result.put("message", iResult.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");

        try {
            response.getWriter().write(result.toJSONString());
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }

        return new ModelAndView();
    }

    //输错密码次数过多
    @ExceptionHandler(ExcessiveAttemptsException.class)
    public ModelAndView excessiveAttemptsExceptionHandler(HttpServletResponse response){
        //异常返回结果
        IResult iResult = LoginResult.TO_MUCH_ERROR;
        //返回异常结果JSON数据
        JSONObject result = new JSONObject();
        result.put("code", iResult.getCode());
        result.put("message", iResult.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");

        try {
            response.getWriter().write(result.toJSONString());
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }

        return new ModelAndView();
    }

}
