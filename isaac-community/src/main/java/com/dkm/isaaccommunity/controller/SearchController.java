package com.dkm.isaaccommunity.controller;

import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.service.LuceneService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/search")
@RequiresRoles(value = {"2"})
public class SearchController {

    @Autowired
    private LuceneService luceneService;


    /**
     * 内容检索请求
     *
     * @param   words  检索关键词
     * @return
     */
    @RequestMapping(value = "/text", method = RequestMethod.GET)
    public String getSearchResult(Model model,@RequestParam("words")String words){
        List<Question> result = luceneService.getSearchResult(words);
        model.addAttribute("result", result);
        return "question/searchResult";
    }
}
