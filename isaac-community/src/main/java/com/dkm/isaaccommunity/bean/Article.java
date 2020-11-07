package com.dkm.isaaccommunity.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Article implements Serializable {
    private Integer id;                 //文章ID
    private String title;               //文章标题
    private String description;         //文章补充描述
    private String url;                 //文章URL
    private LocalDateTime createTime;   //文章创建时间
    private String createTimeToString;  //时间字符串

    public String getCreateTimeToString() {
        return createTimeToString;
    }

    public void setCreateTimeToString(String createTimeToString) {
        this.createTimeToString = createTimeToString;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
