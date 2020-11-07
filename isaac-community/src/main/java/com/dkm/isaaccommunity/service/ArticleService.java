package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Article;

import java.util.List;

public interface ArticleService {
    boolean registArticle(Article article);
    List<Article> showAllArticles();
    Article getArticleById(Integer id);
    boolean updateArticle(Article article);
    boolean deleteArticle(Integer id);
}
