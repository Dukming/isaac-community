package com.dkm.isaaccommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.*;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.service.QuestionService;
import com.dkm.isaaccommunity.service.RedisService;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.service.aop.IndexAnnotation;
import com.dkm.isaaccommunity.util.BaseUtil;
import com.dkm.isaaccommunity.util.FileUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;


@Controller
@RequestMapping("/question")
@RequiresRoles(value = {"2"})
public class QuestionController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model){
        User user = userService.getSessionUser();
        Long collectionCount = redisService.getUserCollectionCount();
        Long attentionCount = redisService.getUserAttentionCount();
        model.addAttribute("user", user);
        model.addAttribute("collectionCount", collectionCount);
        model.addAttribute("attentionCount", attentionCount);
        return "question/questionIndex";
    }

    @RequestMapping(value = "/question", method = RequestMethod.GET)
    public String toAddQuestion(){
        return "question/question";
    }


    @ResponseBody
    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    public JSONObject uploadImg(HttpServletRequest request){
        JSONObject result = new JSONObject();
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multiRequest.getFile("file");

        // 拿到文件名
        String filename = file.getOriginalFilename();

        //判断上传文件格式
        String fileType = file.getContentType();

        // 存放上传图片的文件夹
        File fileDir = FileUtil.getImgDirFile(GlobalConstant.QUESTION_IMG_PATH_PREFIX);

        if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpeg")) {
            try {
                // 构建真实的文件路径
                String localPath = fileDir.getAbsolutePath() + File.separator + filename;
                File newFile = new File(localPath);
                // 上传图片到 -》 “绝对路径”
                file.transferTo(newFile);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @ResponseBody
    @IndexAnnotation(type = GlobalConstant.QUESTION, action = GlobalConstant.ADD)
    @RequestMapping(value = "/addQuestion", method = RequestMethod.POST)
    public JSONObject addQuestion(@RequestBody Question question){
        return BaseUtil.getResult(questionService.addQuestion(question));
    }

    @ResponseBody
    @RequestMapping(value = "/getNewestQuestions", method = RequestMethod.GET)
    public TableData getNewestQuestions(){
        return new TableData(questionService.getNewestQuestions());
    }

    @ResponseBody
    @RequestMapping(value = "/getNewestQuestionsDetails", method = RequestMethod.GET)
    public QuestionsList getNewestQuestionsDetails(@RequestParam("page")int page, @RequestParam("time") long time){
        return  questionService.getNewestQuestionsDetails(page, time);
    }

    @RequestMapping(value = "/toQuestionDetail", method = RequestMethod.GET)
    public String toQuestionDetail(Model model, @PathParam("qid") String qid){
        User user = userService.getSessionUser();
        model.addAttribute("user", user);
        model.addAttribute("qid", qid);
        return "question/questionDetail";
    }

    @ResponseBody
    @RequestMapping(value = "/getQuestionDetail", method = RequestMethod.GET)
    public Question getQuestion(@RequestParam("qid")int qid){
        return questionService.getQuestion(qid);
    }

    @ResponseBody
    @RequestMapping(value = "/getHotQuestionsDetails", method = RequestMethod.GET)
    public QuestionsList getHotQuestionsDetails(@RequestParam("page")int page, @RequestParam("token") String token){
        return questionService.getHotQuestionsByPage(page, token);
    }

    @ResponseBody
    @RequestMapping(value = "/getUserQuestions", method = RequestMethod.GET)
    public QuestionsList getUserQuestions(@RequestParam("page")int page){
        return questionService.getUserQuestions(page);
    }

    @ResponseBody
    @RequestMapping(value = "/attentionQuestion", method = RequestMethod.POST)
    public JSONObject attentionQuestion(@RequestBody Question question){
        if(BaseUtil.isNullOrEmpty(String.valueOf(question.getId()))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(questionService.attentionQuestion(question.getId()));
    }

    @ResponseBody
    @RequestMapping(value = "/cancelAttention", method = RequestMethod.POST)
    public JSONObject cancelAttention(@RequestBody Question question){
        if(BaseUtil.isNullOrEmpty(String.valueOf(question.getId()))){
            return BaseUtil.getResult(BaseResult.FAILTURE);
        }

        return BaseUtil.getResult(questionService.cancelAttentionQuestion(question.getId()));
    }

    @ResponseBody
    @RequestMapping(value = "/getUserAttention", method = RequestMethod.GET)
    public QuestionsList getUserAttention(@RequestParam("page")int page){
        return questionService.getUserAttentions(page);
    }

}
