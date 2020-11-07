package com.dkm.isaaccommunity.bean;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Question implements Serializable {
    @Column
    private Integer id;                 //问题ID
    @Column
    private String title;               //问题标题
    @Column
    private String description;         //问题描述

    private User user;                  //提问者用户

    @Column
    private LocalDateTime createTime;   //问题创建时间
    @Column
    private LocalDateTime updateTime;   //问题更新时间
    private String imgName;             //问题上传图片名

    private Long answersnum;          //问题回答数
    private Double hotIndex;          //热门指数
    private Integer hotuserid;        //点赞数最多的回答用户ID
    private String hotusername;       //点赞数最多的回答用户名称
    private String hotuserheadimg;    //点赞数最多的回答用户头像
    private Integer hotanswerid;      //热门回答ID
    private String hotanswer;         //点赞数最多的回答内容
    private Integer hotstar;          //点赞数最多的回答的点赞数

    public Integer getHotuserid() {
        return hotuserid;
    }

    public void setHotuserid(Integer hotuserid) {
        this.hotuserid = hotuserid;
    }

    public String getHotusername() {
        return hotusername;
    }

    public void setHotusername(String hotusername) {
        this.hotusername = hotusername;
    }

    public String getHotuserheadimg() {
        return hotuserheadimg;
    }

    public void setHotuserheadimg(String hotuserheadimg) {
        this.hotuserheadimg = hotuserheadimg;
    }

    public Integer getHotanswerid() {
        return hotanswerid;
    }

    public void setHotanswerid(Integer hotanswerid) {
        this.hotanswerid = hotanswerid;
    }

    public String getHotanswer() {
        return hotanswer;
    }

    public void setHotanswer(String hotanswer) {
        this.hotanswer = hotanswer;
    }

    public Integer getHotstar() {
        return hotstar;
    }

    public void setHotstar(Integer hotstar) {
        this.hotstar = hotstar;
    }



    public Long getAnswersnum() {
        return answersnum;
    }

    public void setAnswersnum(Long answersnum) {
        this.answersnum = answersnum;
    }

    public Double getHotIndex() {
        return hotIndex;
    }

    public void setHotIndex(Double hotIndex) {
        this.hotIndex = hotIndex;
    }



    public void setAnswerInfo(Answer answer) {
        this.hotuserid = answer.getUser().getId();
        this.hotusername = answer.getUser().getUserName();
        this.hotanswer = answer.getContent();
        this.hotanswerid = answer.getId();
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    @Override
    public String toString() {
        return "Question{" +
                "qid=" + id +
                ", title='" + title + '\'' +
                ", text='" + description + '\'' +
                ", userid=" + user.getId() +
                ", createtime=" + createTime +
                ", updatetime=" + updateTime +
                '}';
    }


}
