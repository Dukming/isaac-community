package com.dkm.isaaccommunity.controller;


import com.dkm.isaaccommunity.bean.Article;
import com.dkm.isaaccommunity.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/article")
@RequiresRoles(value = {"2"})
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "/showArticleList/{toPage}", method = RequestMethod.GET)
    public String showArticleList(Model model, @PathVariable("toPage") Integer toPage){
        PageHelper.startPage(toPage,5);
        List<Article> articleList = articleService.showAllArticles();
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        model.addAttribute("articleList", articleList);
        model.addAttribute("page",pageInfo);
        return "article/articleList";
    }

}
