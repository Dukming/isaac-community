package com.dkm.isaaccommunity.result;

/**
 * 操作结果返回内容定义接口
 */
public interface IResult {
    /**
     * 获取结果标识
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取结果信息
     *
     * @return
     */
    String getMessage();
}
