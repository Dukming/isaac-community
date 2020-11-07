package com.dkm.isaaccommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.*;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.dao.AnswerDao;
import com.dkm.isaaccommunity.dao.QuestionDao;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.result.IResult;
import com.dkm.isaaccommunity.result.QuestionResult;
import com.dkm.isaaccommunity.service.QuestionService;
import com.dkm.isaaccommunity.service.RedisService;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.BaseUtil;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class    QuestionServiceImpl implements QuestionService {
    @Autowired
    private UserService userService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AnswerDao answerDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public QuestionResult addQuestion(Question question) {
        if(BaseUtil.isNullOrEmpty(question.getTitle())){
            return QuestionResult.NULL_TITLE;
        }


        User user = userService.getSessionUser();
        if(null == user){
            throw new UnknownAccountException();
        }

        question.setUser(user);
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());

        //保存问题到DB
        questionDao.insertQuestion(question);
        //缓存问题信息
        redisService.saveQuestionInfo(question);

        return QuestionResult.SUCCESS;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public IResult attentionQuestion(Integer qid) {
        User user = userService.getSessionUser();
        boolean hasAttention = redisService.hasAttentionQuestion(qid);
        if(hasAttention){
            return BaseResult.FAILTURE;
        } else {
            questionDao.insertAttentionQuestion(qid, user.getId(), LocalDateTime.now());
            redisService.attentionQuestion(qid);
            return BaseResult.SUCCESS;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public IResult cancelAttentionQuestion(Integer qid) {
        User user = userService.getSessionUser();
        boolean hasAttention = redisService.hasAttentionQuestion(qid);
        if(hasAttention){
            questionDao.deleteAttentionQuestion(qid, user.getId());
            redisService.attentionQuestion(qid);
            return BaseResult.SUCCESS;
        } else {
            return BaseResult.FAILTURE;
        }
    }

    @Override
    public List<Question> getNewestQuestions() {
        //从Redis中获取最新的10个问题
        Set<Object> questions = redisService.getNewestQuestions();

        List<Question> result = new ArrayList<>();
        //解析问题标题信息
        Iterator<Object> iterator = questions.iterator();
        while (iterator.hasNext()){
            JSONObject json = JSONObject.parseObject(iterator.next().toString());

            Question question = new Question();
            String key = json.keySet().iterator().next();
            question.setId(Integer.valueOf(key));
            question.setTitle((String) json.get(key));

            result.add(question);
        }

        return result;
    }


    @Override
    public QuestionsList getNewestQuestionsDetails(int page, long time) {
        //查询问题列表，每次查询10条
        Set<Object> questions =  redisService.getNewestQuestionsByIndex(page, time);

        if(null == questions || questions.size() < 1){
            return null;
        }

        StringBuilder builder = new StringBuilder();

        //解析问题标题信息
        Iterator<Object> iterator = questions.iterator();
        while (iterator.hasNext()){
            int key = Integer.valueOf(JSONObject.parseObject(iterator.next().toString()).keySet().iterator().next());
            builder.append(key).append(",");
        }

        //去掉最后的逗号，获取查询详细信息的问题ID字符串
        String qids = builder.toString().substring(0, builder.length() - 1);

        //获取指定问题列表的详细信息
        List<Question> questionsList = questionDao.getQuestionsList(qids);
        setQuestionInfo(questionsList);

        //获取要展示的问题总数量
        Long sum = redisService.getNewestQuestionsCountByTime(time);

        return new QuestionsList(questionsList, sum);
    }

    private void setQuestionInfo(List<Question> questionDetailList) {
        for(int i = 0, len = questionDetailList.size(); i < len; i++){
            Question questionDetail = questionDetailList.get(i);

            //设置问题回答数量
            questionDetail.setAnswersnum(redisService.getQuestionAnswersCount(questionDetail.getId()));

            //设置头像地址
            User user = questionDetail.getUser();
            user.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + user.getImgName());
            questionDetail.setUser(user);
            if(questionDetail.getImgName() != null){
                questionDetail.setImgName(GlobalConstant.USER_QUESTION_PRE_ADDR + questionDetail.getImgName());
            }

        }
    }

    @Override
    @Cacheable(value = "questionCache")
    public Question getQuestion(int qid) {
        //查询问题详情
        Question question =  questionDao.selectQuestionById(qid);
        //设置问题的回答数
        question.setAnswersnum(redisService.getQuestionAnswersCount(qid));
        if(question.getImgName() != null){
            question.setImgName(GlobalConstant.USER_QUESTION_PRE_ADDR + question.getImgName());
        }
        return question;
    }

    @Override
    public Question getQuestionAndAnswer(int qid, int aid) {
        //查询问题详情
        Question question =  questionDao.selectQuestionById(qid);
        //设置问题的回答数
        question.setAnswersnum(redisService.getQuestionAnswersCount(qid));
        if(question.getImgName() != null){
            question.setImgName(GlobalConstant.USER_QUESTION_PRE_ADDR + question.getImgName());
        }
        //设置问题回答内容
        question.setHotanswer(answerDao.getAnswer(aid).getContent());
        return question;
    }

    @Override
    public QuestionsList getHotQuestionsByPage(int page, String token) {
        if(page < 1){
            return null;
        }

        //从redis缓存中获取最热门的10条问题
        Set<ZSetOperations.TypedTuple<Object>> questions = redisService.getHotQuestionsFromRedis(page, token);
        if(null == questions || questions.size() < 1){
            return new QuestionsList(null, Long.valueOf(0), null);
        }

        //获取热门问题详情
        List<Question> questionsList = getQuestions(questions);

        //设置热门问题点赞数最高的回答信息
        setQuestionHotAnswer(questionsList);

        //获取当前时刻热门问题的总数量
        Long num = redisService.getHotQuestionsCount();

        return new QuestionsList(questionsList, num, "HASHOTZSETCACHE");
    }

    private List<Question> getQuestions(Set<ZSetOperations.TypedTuple<Object>> questions) {
        StringBuilder builder = new StringBuilder();

        //获取当前页问题ID
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = questions.iterator();
        while (iterator.hasNext()){
            builder.append(iterator.next().getValue()).append(",");
        }
        //去掉最后的逗号，获取查询详细信息的问题ID字符串
        String qids = builder.toString().substring(0, builder.length() - 1);

        //获取指定问题列表的详细信息
        List<Question> questionList = questionDao.getQuestionsList(qids);

        //设置问题的热门指数
        Iterator<ZSetOperations.TypedTuple<Object>> iterator2 = questions.iterator();
        while (iterator2.hasNext()){
            ZSetOperations.TypedTuple<Object> zsetKV = iterator2.next();

            //先根据问题ID获取问题，再设置热门指数
            Question question =  getQuestionFromListByQid(questionList, (Integer) zsetKV.getValue());
            if(null != question){
                question.setHotIndex(zsetKV.getScore());
            }else {
                question.setHotIndex(Double.valueOf(0));
            }
        }

        //按照热门指数重新排序
        questionList.sort(new Comparator<Question>() {
            @Override
            public int compare(Question qus1, Question qus2) {
                int res = Double.valueOf(qus2.getHotIndex() - qus1.getHotIndex()).intValue();
                if(0 == res){
                    Duration duration = Duration.between(qus1.getCreateTime(),qus2.getCreateTime());
                    Long t = duration.toMillis();
                    return t.intValue();
                }

                return  res;
            }
        });

        return questionList;
    }

    private Question getQuestionFromListByQid(List<Question> questionList, Integer qid) {
        if(null == questionList || 0 == questionList.size() || null == qid || 0 == qid.intValue()){
            return null;
        }

        for (int i = 0, len = questionList.size(); i < len; i++) {
            if(questionList.get(i).getId().intValue() == qid.intValue()){
                return questionList.get(i);
            }
        }

        return null;
    }

    private void setQuestionHotAnswer(List<Question> questionList) {
        if(null == questionList || 0 == questionList.size()){
            return;
        }

        for(int i = 0, len = questionList.size(); i < len; i++){
            //点赞数最多的回答
            ZSetOperations.TypedTuple<Object> hotAnswer = redisService.getQuestionMostStarAnswer(questionList.get(i));
            if(null == hotAnswer){
                continue;
            }

            //获取点赞数最多回答详情
            Answer answer = answerDao.getAnswer((Integer) hotAnswer.getValue());

            //设置问题点赞最多回答信息
            questionList.get(i).setAnswerInfo(answer);
            questionList.get(i).setHotstar(hotAnswer.getScore().intValue());

            //设置提问者头像
            User user = questionList.get(i).getUser();
            user.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + user.getImgName());
            questionList.get(i).setUser(user);

            //设置点赞数最多回答人员头像
            questionList.get(i).setHotuserheadimg(
                    GlobalConstant.USER_HEAD_PRE_ADDR + userService.getUser(questionList.get(i).getHotuserid()).getImgName());

            //设置问题图片地址
            if(questionList.get(i).getImgName() != null){
                questionList.get(i).setImgName(GlobalConstant.USER_QUESTION_PRE_ADDR + questionList.get(i).getImgName());
            }
        }
    }

    @Override
    public QuestionsList getUserQuestions(int page) {
        Integer userId = userService.getSessionUser().getId();

        //分页查询用户问题
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.QUESTIONS_NUM);
        pageInfo.setUserid(userId);
        List<Question> questionsList = questionDao.getUserQuestions(pageInfo);

        //获取用户提问的所有问题数量
        Long questionsCount = redisService.getUserQuestionsCount(userId);

        if(questionsCount < questionsList.size()){  // 缓存失效
            questionsCount = questionDao.selectUserQuestionCount(userId).longValue();
        }

        //设置问题回答数和图片地址
        for(int i = 0, len = questionsList.size(); i < len; i++){
            Integer qid = questionsList.get(i).getId();
            questionsList.get(i).setAnswersnum(questionDao.selectQuestionAnswerCount(qid).longValue());
            //设置问题图片地址
            if(questionsList.get(i).getImgName() != null){
                questionsList.get(i).setImgName(GlobalConstant.USER_QUESTION_PRE_ADDR + questionsList.get(i).getImgName());
            }
        }

        return new QuestionsList(questionsList, questionsCount);
    }

    @Override
    public QuestionsList getUserAttentions(int page) {
        User user = userService.getSessionUser();

        //分页查询用户关注
        PageInfo pageInfo = new PageInfo(page, GlobalConstant.QUESTIONS_NUM);
        pageInfo.setUserid(user.getId());
        List<Question> questionsList = questionDao.getUserAttentions(pageInfo);
        for(int i = 0, len = questionsList.size(); i < len; i++){
            User questionUser = questionsList.get(i).getUser();
            questionUser.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + questionUser.getImgName());
            questionsList.get(i).setUser(questionUser);
            questionsList.get(i).setAnswersnum(redisService.getQuestionAnswersCount(questionsList.get(i).getId()));
        }

        Integer attentionCount = questionDao.selectUserAttentionCount(user.getId());
        return new QuestionsList(questionsList,attentionCount.longValue());

    }

}
