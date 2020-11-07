package com.dkm.isaaccommunity.result;

/**
 * 通用返回结果状态
 */
public enum BaseResult implements IResult {
    //成功
    SUCCESS("成功", 1) ,

    //失败
    FAILTURE("失败", 2),

    //不合法
    NOT_LEGAL("内容含有敏感词汇", 3);

    //结果信息
    private  String message;
    //结果编码
    private  int code;

    BaseResult(String message, int code){
        this.message = message;
        this.code = code;
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
