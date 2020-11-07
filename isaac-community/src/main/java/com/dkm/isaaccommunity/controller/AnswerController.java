package com.dkm.isaaccommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.*;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.service.AnswerService;
import com.dkm.isaaccommunity.service.QuestionService;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.service.aop.IndexAnnotation;
import com.dkm.isaaccommunity.util.BaseUtil;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("/answer")
@RequiresRoles(value = {"2"})
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @ResponseBody
    @RequestMapping(value = "/getQuestionAllAnswers", method = RequestMethod.GET)
    public AnswersList getQuestionAllAnswers(@RequestParam("page")int page, @RequestParam("qid")int qid){
        if(BaseUtil.isNullOrEmpty(Integer.toString(qid))){
            return null;
        }

        return answerService.getQuestionAllAnswers(page, qid);
    }

    @ResponseBody
    @IndexAnnotation(type = GlobalConstant.ANSWER, action = GlobalConstant.ADD)
    @RequestMapping(value = "/addAnswer", method = RequestMethod.POST)
    public JSONObject addAnswer(@RequestBody Answer answer){
        if(BaseUtil.isNullOrEmpty(String.valueOf(answer.getQuestionID())) || BaseUtil.isNullOrEmpty(answer.getContent())){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(answerService.addAnswer(answer));
    }

    @ResponseBody
    @RequestMapping(value = "/getUserAnswers", method = RequestMethod.GET)
    public QuestionsList getUserAnswers(@RequestParam("page")int page){
        return answerService.getUserAnswers(page);
    }


    @ResponseBody
    @RequestMapping(value = "/starAction", method = RequestMethod.GET)
    public JSONObject starAction(@RequestParam("aid")Integer aid, @RequestParam("qid")Integer qid){
        if(BaseUtil.isNullOrEmpty(String.valueOf(aid))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stars", answerService.starAction(aid, qid));

        return jsonObject;
    }

    @RequestMapping(value = "/toEditAnswer", method = RequestMethod.GET)
    public String toEditAnswer(Model model, @PathParam("qid") String qid, @PathParam("aid") String aid){
        User user = userService.getSessionUser();
        model.addAttribute("user", user);
        model.addAttribute("qid", qid);
        model.addAttribute("aid", aid);
        return "question/editAnswer";
    }

    @ResponseBody
    @RequestMapping(value = "/showAnswer", method = RequestMethod.GET)
    public Question showAnswer(@RequestParam("qid")int qid, @RequestParam("aid")int aid){
        return questionService.getQuestionAndAnswer(qid, aid);
    }

    @ResponseBody
    @RequestMapping(value = "/editAnswer", method = RequestMethod.POST)
    public JSONObject editAnswer(@RequestBody Answer answer){
        if(BaseUtil.isNullOrEmpty(String.valueOf(answer.getId())) || BaseUtil.isNullOrEmpty(answer.getContent())){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(answerService.editAnswer(answer));
    }

    @ResponseBody
    @RequestMapping(value = "/collectAnswer", method = RequestMethod.POST)
    public JSONObject collectAnswer(@RequestBody Answer answer){
        if(BaseUtil.isNullOrEmpty(String.valueOf(answer.getId()))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(answerService.collectAnswer(answer.getId()));
    }

    @ResponseBody
    @RequestMapping(value = "/cancelCollect", method = RequestMethod.POST)
    public JSONObject cancelCollect(@RequestBody Answer answer){
        if(BaseUtil.isNullOrEmpty(String.valueOf(answer.getId()))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(answerService.cancelCollectAnswer(answer.getId()));
    }

    @ResponseBody
    @RequestMapping(value = "/getUserCollection", method = RequestMethod.GET)
    public QuestionsList getUserCollection(@RequestParam("page")int page){
        return answerService.getUserCollections(page);
    }

}
