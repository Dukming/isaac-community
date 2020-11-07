package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.Item;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDao {
    Item getItem(Integer id);
    List<Item> selectAllItem();
    List<Item> selectItemByNameAndType(@Param("itemName") String name, @Param("tagName") String tagName, @Param("typeName") String typeName);
    List<Item> selectItemByTagName(@Param("name") String name);
    Item selectItemByImgName(@Param("imgName") String imgName);
    int updateItem(Item item);
    int insertItem(Item item);
}
