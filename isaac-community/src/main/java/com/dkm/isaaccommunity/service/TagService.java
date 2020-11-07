package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Tag;

import java.util.List;

public interface TagService {
    List<Tag> showAllTags();
    boolean registTag(String tagName);
    boolean deleteTag(int id);
    boolean updateTag(int id, String name);
}
