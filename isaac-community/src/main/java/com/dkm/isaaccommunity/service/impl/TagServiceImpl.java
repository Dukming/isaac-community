package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.Tag;
import com.dkm.isaaccommunity.dao.TagDao;
import com.dkm.isaaccommunity.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagDao tagDao;

    @Override
    public List<Tag> showAllTags() {
        return tagDao.getAllTag();
    }

    @Override
    public boolean registTag(String tagName) {
        if(tagDao.insertTag(tagName) <= 0){
            return false;
        }
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public boolean deleteTag(int id) {
        tagDao.deleteMapByti(id);
        if(tagDao.deleteTag(id) <= 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean updateTag(int id, String name) {
        if(tagDao.updateTag(id,name) <= 0){
            return false;
        }
        return true;
    }


}
