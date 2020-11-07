package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.Article;
import com.dkm.isaaccommunity.dao.ArticleDao;
import com.dkm.isaaccommunity.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Override
    public boolean registArticle(Article article) {
        article.setCreateTime(LocalDateTime.now());
        if(articleDao.insertArticle(article) <= 0){
            return false;
        }
        return true;
    }

    @Override
    public List<Article> showAllArticles() {
        List<Article> articleList = articleDao.selectAllArticles();
        for(Article article : articleList){
            String timeString = article.getCreateTime().format(DateTimeFormatter.ISO_DATE);
            article.setCreateTimeToString(timeString);
        }
        return articleList;
    }

    @Override
    public Article getArticleById(Integer id) {
        return articleDao.selectArticleById(id);
    }

    @Override
    public boolean updateArticle(Article article) {
        if(articleDao.updateArticle(article) <= 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteArticle(Integer id) {
        if(articleDao.deleteArticle(id) <= 0){
            return false;
        }
        return true;
    }
}
