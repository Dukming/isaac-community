package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.result.IResult;

import java.util.List;


/**
 * Lucene处理逻辑接口定义
 */
public interface LuceneService {
    /**
     * 添加索引
     *
     * @param object
     * @return
     */
    IResult addIndex(Object object);

    /**
     * 根据关键词检索内容
     *
     * @return
     * @param words
     */
    List<Question> getSearchResult(String words);
}
