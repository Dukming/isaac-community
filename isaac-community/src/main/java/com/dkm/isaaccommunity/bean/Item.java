package com.dkm.isaaccommunity.bean;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
    private Integer id ;        //物品ID
    private String name;        //物品名称
    private String imgName;     //图片名称
    private String imgAddr;     //图片地址
    private String description; //物品描述
    private String typeName;    //物品种类
    private List<Tag> tags;     //该物品所拥有的标签

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getImgAddr() {
        return imgAddr;
    }

    public void setImgAddr(String imgAddr) {
        this.imgAddr = imgAddr;
    }
}
