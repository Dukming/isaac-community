package com.dkm.isaaccommunity.controller;

import com.dkm.isaaccommunity.bean.Article;
import com.dkm.isaaccommunity.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping("/adminArticle")
@RequiresRoles(value = {"1"})
public class ArticleAdminController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "/publishArticle",method = RequestMethod.GET )
    public String publishArticle(){
        return "adminArticle/adminPublishArticle";
    }

    @ResponseBody
    @RequestMapping(value = "/registArticle",method = RequestMethod.POST )
    public String registArticle(@ModelAttribute Article article){
        if(articleService.registArticle(article)){
            return "<script>alert('regist article success!');window.location.href='/adminArticle/showArticleList/1';</script>";
        }else{
            return "<script>alert('regist article failed!!');window.location.href='/adminArticle/publishArticle';</script>";
        }
    }

    @RequestMapping(value = "/showArticleList/{toPage}", method = RequestMethod.GET)
    public String showArticleList(Model model, @PathVariable("toPage") Integer toPage){
        PageHelper.startPage(toPage,5);
        List<Article> articleList = articleService.showAllArticles();
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        model.addAttribute("articleList", articleList);
        model.addAttribute("page",pageInfo);
        return "adminArticle/adminArticleList";
    }

    @RequestMapping(value = "/toEditArticle",method = RequestMethod.GET )
    public String toEditArticle(Model model, @PathParam("id") Integer id){
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "adminArticle/adminEditArticle";
    }

    @ResponseBody
    @RequestMapping(value = "/editArticle",method = RequestMethod.POST )
    public String editArticle(@ModelAttribute Article article){
        if(articleService.updateArticle(article)){
            return "<script>alert('Update article success!');window.location.href='/adminArticle/showArticleList/1';</script>";
        }else{
            return "<script>alert('Update article failed!!');window.location.href='/adminArticle/toEditArticle';</script>";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteArticle",method = RequestMethod.GET )
    public String deleteArticle(@PathParam("id") Integer id){
        if(articleService.deleteArticle(id)){
            return "<script>alert('Delete article success!');window.location.href='/adminArticle/showArticleList/1';</script>";
        }else{
            return "<script>alert('Delete article failed!!');window.location.href='/adminArticle/showArticleList/1';</script>";
        }
    }

}
