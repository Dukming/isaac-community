package com.dkm.isaaccommunity.dao;


import com.dkm.isaaccommunity.bean.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagDao {
    int insertTag(@Param("name") String name);
    int updateTag(@Param("id") int id, @Param("name") String name);
    int insertMap(@Param("itemID") int itemID,@Param("tagID") int tagID);
    int deleteMapByti(@Param("tagID") int tagID);
    int deleteMapByii(@Param("itemID") int itemID);
    int deleteTag(@Param("id") int id);
    Tag getTag(@Param("name") String name);
    List<Tag> getAllTag();
}
