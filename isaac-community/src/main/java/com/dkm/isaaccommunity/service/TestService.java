package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Item;

import java.io.IOException;
import java.util.List;

public interface TestService {
    void showScore(String filePath) throws IOException;
    List<Item> showAllItems();
}
