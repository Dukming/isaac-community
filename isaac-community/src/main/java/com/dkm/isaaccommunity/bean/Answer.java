package com.dkm.isaaccommunity.bean;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Answer implements Serializable {
    @Column
    private Integer id;                 //回答ID
    @Column
    private String content;             //回答内容
    @Column
    private Integer questionID;         //对应问题ID

    private User user;                  //回答者用户

    @Column
    private LocalDateTime createTime;   //回答创建时间
    @Column
    private Integer star;               //点赞数

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

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
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

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }
}
