package com.dkm.isaaccommunity.result;

/**
 * 用户登录结果返回状态
 */
public enum LoginResult implements IResult {
    //成功
    SUCCESS("登录成功", 1) ,

    //密码错误
    INCORRECT_PWD("密码错误", 2),

    //输错密码次数过多
    TO_MUCH_ERROR("输错密码达5次", 3),

    //账号不存在
    NO_ACCOUNT("账号不存在", 4),

    //账号或密码为空
    NULL_NAME_PWD("账号或密码为空", 5),

    //未知原因登录失败
    LOGIN_FAILTURE("登录失败，请重试", 6),

    //权限不足
    NO_AUTHORIZED("权限不足",7),

    //账号被锁定
    LOCKED_ACCOUNT("登录失败，请重试", 8);

    //结果信息
    private  String message;
    //结果编码
    private  int code;

    LoginResult(String message, int code){
        this.message = message;
        this.code = code;
    }

    public static String getMessage(int code){
        for (LoginResult resultCode : LoginResult.values()){
            if(resultCode.getCode() == code){
                return resultCode.getMessage();
            }
        }

        return  null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
