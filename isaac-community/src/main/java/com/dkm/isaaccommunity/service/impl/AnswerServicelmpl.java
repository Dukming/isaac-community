package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.*;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.dao.AnswerDao;
import com.dkm.isaaccommunity.dao.QuestionDao;
import com.dkm.isaaccommunity.dao.UserDao;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.result.IResult;
import com.dkm.isaaccommunity.service.AnswerService;
import com.dkm.isaaccommunity.service.RedisService;
import com.dkm.isaaccommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerServicelmpl implements AnswerService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional
    @CacheEvict(value = "questionCache", key = "#answer.questionID", beforeInvocation = true)
    @Override
    public IResult addAnswer(Answer answer) {
        User user = userService.getSessionUser();
        //设置回答用户信息
        answer.setUser(user);
        //设置回答创建时间
        answer.setCreateTime(LocalDateTime.now());
        //初始化问题点赞数
        answer.setStar(0);

        //保存回答
        answerDao.insertAnswer(answer);
        //保存相关信息到redis
        redisService.saveAnswerInfo(answer);

        return BaseResult.SUCCESS;
    }

    @Transactional
    @CacheEvict(value = "questionCache", key = "#answer.questionID", beforeInvocation = true)
    @Override
    public IResult editAnswer(Answer answer) {
        answerDao.updateAnswerContent(answer.getId(), answer.getContent());
        return BaseResult.SUCCESS;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public IResult collectAnswer(Integer aid) {
        User user = userService.getSessionUser();
        boolean hasCollect = redisService.hasCollectAnswer(aid);
        if(hasCollect){
            return BaseResult.FAILTURE;
        }else{
            answerDao.insertCollectAnswer(aid, user.getId(), LocalDateTime.now());
            redisService.collectAnswer(aid);
            return BaseResult.SUCCESS;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public IResult cancelCollectAnswer(Integer aid) {
        User user = userService.getSessionUser();
        boolean hasCollect = redisService.hasCollectAnswer(aid);
        if(hasCollect){
            answerDao.deleteCollectAnswer(aid, user.getId());
            redisService.collectAnswer(aid);
            return BaseResult.SUCCESS;
        }else{
            return BaseResult.FAILTURE;
        }
    }

    @Override
    public AnswersList getQuestionAllAnswers(int page, int qid) {

        if(null == Integer.valueOf(page) || page < 1){
            page = 1;
        }

        //设置分页信息
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.ANSWERS_NUM, qid);
        //查询问题回答
        List<Answer> answers = answerDao.getQuestionAllAnswers(pageInfo);

        //设置回答用户头像地址
        for (int i = 0, len = answers.size(); i < len; i++) {
            User user = answers.get(i).getUser();
            user.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + user.getImgName());
            answers.get(i).setUser(user);
        }

        //获取问题回答的总数
        Long sum = redisService.getQuestionAnswersCount(qid);

        return new AnswersList(answers,  sum);

    }

    @Override
    public QuestionsList getUserAnswers(int page) {
        User user = userService.getSessionUser();

        //分页查询用户回答
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.QUESTIONS_NUM);
        pageInfo.setUserid(user.getId());
        List<Answer> answersList = answerDao.getUserAnswers(pageInfo);
        List<Question> questionsList = new ArrayList<>();
        for(int i = 0, len = answersList.size(); i < len; i++){
            Integer qid = answersList.get(i).getQuestionID();
            questionsList.add(questionDao.selectQuestionById(qid));
            questionsList.get(i).setHotuserheadimg(user.getImgAddr());
            questionsList.get(i).setHotusername(user.getUserName());
            questionsList.get(i).setHotanswerid(answersList.get(i).getId());
            questionsList.get(i).setHotstar(answersList.get(i).getStar());
            questionsList.get(i).setHotanswer(answersList.get(i).getContent());
            questionsList.get(i).setCreateTime(answersList.get(i).getCreateTime());
        }

        Integer answerCount = answerDao.selectUserAnswerCount(user.getId());
        return new QuestionsList(questionsList,answerCount.longValue());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public Long starAction(Integer aid, Integer qid) {
        //判断用户是否对当前回答点赞过
        boolean hasStared =  redisService.hasStaredAnswer(aid);
        //获取问题点赞数
        Long stars = redisService.getAnswerStars(qid, aid);

        //redis中进行点赞相关的处理
        redisService.staredAnswer(qid ,aid);

        //获取回答者ID
        Integer userID = answerDao.getAnswer(aid).getUser().getId();

        if(hasStared){
            //已经点赞过
            //数据库中回答点赞数减 1
            answerDao.decreaseStarsNum(aid);
            userDao.decreaseUserScore(userID, GlobalConstant.USER_STAR_SCORE);
            return stars - 1;
        }else{
            //没有点赞过
            //数据库中回答点赞数加 1
            answerDao.increaseStarsNum(aid);
            userDao.increaseUserScore(userID, GlobalConstant.USER_STAR_SCORE);
            return stars + 1;
        }
    }

    @Override
    public QuestionsList getUserCollections(int page) {
        User user = userService.getSessionUser();

        //分页查询用户收藏
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.QUESTIONS_NUM);
        pageInfo.setUserid(user.getId());
        List<Answer> answersList = answerDao.getUserCollections(pageInfo);
        List<Question> questionsList = new ArrayList<>();
        for(int i = 0, len = answersList.size(); i < len; i++){
            Integer qid = answersList.get(i).getQuestionID();
            User answerUser = answersList.get(i).getUser();
            answerUser.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + answerUser.getImgName());
            questionsList.add(questionDao.selectQuestionById(qid));
            questionsList.get(i).setHotuserheadimg(answerUser.getImgAddr());
            questionsList.get(i).setHotusername(answerUser.getUserName());
            questionsList.get(i).setHotanswerid(answersList.get(i).getId());
            questionsList.get(i).setHotstar(answersList.get(i).getStar());
            questionsList.get(i).setHotanswer(answersList.get(i).getContent());
        }

        Integer collecitonCount = answerDao.selectUserCollectionCount(user.getId());
        return new QuestionsList(questionsList,collecitonCount.longValue());
    }

}
