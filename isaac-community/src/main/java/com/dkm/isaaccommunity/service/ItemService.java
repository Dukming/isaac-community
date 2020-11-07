package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Item;

import java.io.IOException;
import java.util.List;


public interface ItemService {
    List<Item> showAllItems();
    List<Item> showItemByNameOrTagAndType(String itemNameOrTagName, String typeName);
    List<Item> showItemByTag(String tagNme);
    List<Item> showItemByImg(String filePath) throws IOException;
    Item showItemByID(Integer id);
    boolean updateItem(Item item);
    boolean registItem(Item item);
}
