package com.dkm.isaaccommunity.dao;

import com.dkm.isaaccommunity.bean.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleDao {
    int insertArticle(Article article);
    List<Article> selectAllArticles();
    Article selectArticleById(Integer id);
    int updateArticle(Article article);
    int deleteArticle(Integer id);
}
