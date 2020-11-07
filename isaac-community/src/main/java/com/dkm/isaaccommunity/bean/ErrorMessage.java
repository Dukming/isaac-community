package com.dkm.isaaccommunity.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ErrorMessage implements Serializable {
    private Integer id;                     //错误信息ID
    private String content;                 //内容
    private User user;                      //报错用户
    private LocalDateTime createTime;       //创建时间
    private Boolean deal;                   //是否被处理(默认0)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Boolean getDeal() {
        return deal;
    }

    public void setDeal(Boolean deal) {
        this.deal = deal;
    }
}
