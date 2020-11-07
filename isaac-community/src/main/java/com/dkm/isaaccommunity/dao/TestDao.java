package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.Item;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDao {
    List<Item> selectAllItem();
}
