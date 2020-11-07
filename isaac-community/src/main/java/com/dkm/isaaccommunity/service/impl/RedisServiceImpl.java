package com.dkm.isaaccommunity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.service.RedisService;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.BaseUtil;
import com.dkm.isaaccommunity.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.dkm.isaaccommunity.config.GlobalConstant.REDIS_ZSET_QUESTIONS_TIME;

@Service
public class RedisServiceImpl implements RedisService {
    //问题初始化热门指数
    private static final double QUESTION_HOT_INDEX = 0.0;
    //回答一个问题热门指数增加的值
    private static final double ANSWER_HOT_INDEX = 10.0;
    //点赞一次对应回答的问题热门指数增加的值
    private static final double STAR_HOT_INDEX = 1.0;
    //取消点赞对应回答的问题热门指数减少的值
    private static final double DE_STAR_HOT_INDEX = -1.0;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;


    @Override
    public void saveHasRegisterPhone(User user){
        redisUtil.addSetValue(GlobalConstant.REDIS_SET_HASREGISTERPHONE, user.getPhone());
    }



    @Override
    public boolean validatePhoneHasRegister(String phone) {
        if(BaseUtil.isNullOrEmpty(phone)){
            return false;
        }

        return  redisUtil.existInSet(GlobalConstant.REDIS_SET_HASREGISTERPHONE, phone);
    }

    @Override
    public void saveQuestionInfo(Question question){
        JSONObject questionInfo = new JSONObject();
        questionInfo.put(String.valueOf(question.getId()), question.getTitle());

        //保存问题时间戳
        redisUtil.addZsetValue(REDIS_ZSET_QUESTIONS_TIME,
                questionInfo.toJSONString() , Double.valueOf(System.currentTimeMillis()));

        //保存用户问题信息
        redisUtil.addSetValue(BaseUtil.getUserQuestionsKey(userService.getSessionUser().getId()), question.getId());

        //初始化问题热门指数
        redisUtil.addZsetValue(GlobalConstant.REDIS_ZSET_QUESTIONS_HOT, question.getId(), this.QUESTION_HOT_INDEX);
    }

    @Override
    public Long getHotQuestionsCount() {
        //当前用户查询最热门问题有序集合key
        String cacheHotQuestionsKey = BaseUtil.getCacheHotQuestionsKey(userService.getSessionUser().getId());

        if(!redisUtil.existZset(cacheHotQuestionsKey)){
            return Long.valueOf(0);
        }

        return redisUtil.getZsetCount(cacheHotQuestionsKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getHotQuestionsFromRedis(int page, String token) {
        //当前用户查询最热门问题有序集合key
        User user = userService.getSessionUser();
        String cacheHotQuestionsKey = BaseUtil.getCacheHotQuestionsKey(user.getId());

        //没有缓存标识，说明之前未请求过；或者没有目标zset
        if(BaseUtil.isNullOrEmpty(token) || !redisUtil.existZset(cacheHotQuestionsKey)){
            //如果刷新页面重新请求，需要先把旧的缓存清除
            redisUtil.deleteKey(cacheHotQuestionsKey);
            //获取最热门问题集合，并缓存
            redisUtil.cacheSortedZset(GlobalConstant.REDIS_ZSET_QUESTIONS_HOT, cacheHotQuestionsKey);
        }

        //计算索引开始位置
        int index = (page - 1) * GlobalConstant.QUESTIONS_NUM;

        //查询问题列表，每次查询10条
        return redisUtil.getZsetMaxKeysOfScores(cacheHotQuestionsKey, index, index + GlobalConstant.QUESTIONS_NUM - 1);
    }

    @Override
    public ZSetOperations.TypedTuple<Object> getQuestionMostStarAnswer(Question question){
        if(null == question){
            return null;
        }

        //获取问题点赞数最多的回答
        Set<ZSetOperations.TypedTuple<Object>> hotAnswers = redisUtil.getZsetMaxKeysInScoresOfScoreInfoWithPage(
                BaseUtil.getRedisQuestionAnswersKey(question.getId()),
                Double.valueOf(0.0),
                Double.POSITIVE_INFINITY,
                Long.valueOf(0),
                Long.valueOf(1));

        if(null == hotAnswers || 0 == hotAnswers.size()){
            return null;
        }

        //点赞数最多的回答
        return hotAnswers.iterator().next();
    }

    @Override
    public Set<Object> getNewestQuestions(){
        return redisUtil.getZsetMaxKeys(REDIS_ZSET_QUESTIONS_TIME, 0 , Long.valueOf(GlobalConstant.QUESTIONS_NUM -1));
    }

    @Override
    public Long getNewestQuestionsCountByTime(long time) {
        if(time < 0){
            return Long.valueOf(0);
        }

        return redisUtil.getZsetCount(REDIS_ZSET_QUESTIONS_TIME, 0.0, time);
    }

    @Override
    public Set<Object> getNewestQuestionsByIndex(int page, long time){
        if(page < 1){
            return null;
        }

        //计算索引开始位置
        int index = (page - 1) * GlobalConstant.QUESTIONS_NUM;

        //查询问题列表，每次查询10条
        return  redisUtil.getZsetMaxKeysInScoresWithPage(REDIS_ZSET_QUESTIONS_TIME,
                0.0, Double.valueOf(time), Long.valueOf(index), GlobalConstant.QUESTIONS_NUM);
    }

    @Override
    public Long getUserQuestionsCount(Integer userId){
        //获取用户提问的所有问题数量
        return redisUtil.getSetCount(BaseUtil.getUserQuestionsKey(userId));
    }

    /**
     * <p>
     *  保存相关信息到redis <br/>
     *
     *  1、新增回答记录到问题zset中 ，需要初始化点赞数为 0 <br/>
     *  2、修改回答对应的问题热门指数 <br/>
     * </p>
     *
     * @param answer 新增的问题回答
     */
    @Override
    public void saveAnswerInfo(Answer answer) {
        //新增回答记录到问题zset中
        redisUtil.addZsetValue(BaseUtil.getRedisQuestionAnswersKey(answer.getQuestionID()), answer.getId(), 0.0);

        //增加问题热门指数
        synchronized(this){
            redisUtil.increaseZsetScore(GlobalConstant.REDIS_ZSET_QUESTIONS_HOT, answer.getQuestionID() , this.ANSWER_HOT_INDEX);
        }
    }

    @Override
    public Long getQuestionAnswersCount(Integer qid){
        if(null == qid){
            return null;
        }

        return redisUtil.getZsetCount(BaseUtil.getRedisQuestionAnswersKey(qid), Double.valueOf(0), Double.POSITIVE_INFINITY);
    }

    @Override
    public Long getUserCollectionCount() {
        User user = userService.getSessionUser();
        Object userCollectAnswers = BaseUtil.getUserCollectAnswersKey(user.getId());
        return redisUtil.getSetCount(userCollectAnswers);
    }

    @Override
    public Long getUserAttentionCount() {
        User user = userService.getSessionUser();
        Object userAttentionQuestions = BaseUtil.getUserAttentionQuestionsKey(user.getId());
        return redisUtil.getSetCount(userAttentionQuestions);
    }

    @Override
    public void staredAnswer(Integer qid, Integer aid){
        //获取用户点赞的回答SET键名
        User user = userService.getSessionUser();
        Object  userStarAnswers = BaseUtil.getUserStaredAnswersKey(user.getId());
        //判断用户是否对当前回答点赞过
        boolean hasStared =  redisUtil.existInSet(userStarAnswers, aid);

        //问题回答redis键名
        Object questionAnswersRedisKey = BaseUtil.getRedisQuestionAnswersKey(qid);
        //获取问题点赞数
        Long stars = redisUtil.getZsetKeyValue(questionAnswersRedisKey, aid).longValue();

        //已经点赞过
        if(hasStared){
            //redis 中回答点赞数减 1
            redisUtil.increaseZsetScore(questionAnswersRedisKey, aid, Double.valueOf(-1));
            //更新问题热门指数
            redisUtil.increaseZsetScore(GlobalConstant.REDIS_ZSET_QUESTIONS_HOT, qid, this.DE_STAR_HOT_INDEX);
            //去除用户点赞的回答
            redisUtil.removeSetValue(userStarAnswers, aid);
        }
        //没有点赞过
        else{
            //redis 中回答点赞数加 1
            redisUtil.increaseZsetScore(questionAnswersRedisKey, aid, Double.valueOf(1));
            //更新问题热门指数
            redisUtil.increaseZsetScore(GlobalConstant.REDIS_ZSET_QUESTIONS_HOT, qid, this.STAR_HOT_INDEX);
            //缓存用户点赞的回答
            redisUtil.addSetValue(userStarAnswers, aid);
        }
    }

    @Override
    public void collectAnswer(Integer aid) {
        //获取用户收藏的回答SET键名
        User user = userService.getSessionUser();
        Object userCollectAnswers = BaseUtil.getUserCollectAnswersKey(user.getId());
        //判断用户是否对当前回答收藏过
        boolean hasCollect = redisUtil.existInSet(userCollectAnswers, aid);
        //收藏过
        if(hasCollect){
            redisUtil.removeSetValue(userCollectAnswers, aid);
        }
        //没有收藏过
        else{
            redisUtil.addSetValue(userCollectAnswers, aid);
        }
    }

    @Override
    public void attentionQuestion(Integer qid) {
        //获取用户关注的问题SET键名
        User user = userService.getSessionUser();
        Object userAttentionQuestions = BaseUtil.getUserAttentionQuestionsKey(user.getId());
        //判断用户是否对当前问题关注过
        boolean hasAttention = redisUtil.existInSet(userAttentionQuestions, qid);
        //关注过
        if(hasAttention){
            redisUtil.removeSetValue(userAttentionQuestions, qid);
        }
        //没有关注过
        else{
            redisUtil.addSetValue(userAttentionQuestions, qid);
        }
    }

    @Override
    public boolean hasStaredAnswer(Integer aid) {
        //获取用户点赞的回答SET键名
        User user = userService.getSessionUser();
        Object  userStarAnswers = BaseUtil.getUserStaredAnswersKey(user.getId());
        //判断用户是否对当前回答点赞过
        boolean hasStared =  redisUtil.existInSet(userStarAnswers, aid);
        return hasStared;
    }

    @Override
    public boolean hasCollectAnswer(Integer aid) {
        //获取用户收藏的回答SET键名
        User user = userService.getSessionUser();
        Object userCollectAnswers = BaseUtil.getUserCollectAnswersKey(user.getId());
        //判断用户是否对当前回答收藏过
        boolean hasCollect = redisUtil.existInSet(userCollectAnswers, aid);
        return hasCollect;
    }

    @Override
    public boolean hasAttentionQuestion(Integer qid) {
        //获取用户关注的问题SET键名
        User user = userService.getSessionUser();
        Object userAttentionQuestions = BaseUtil.getUserAttentionQuestionsKey(user.getId());
        //判断用户是否对当前问题关注过
        boolean hasAttention = redisUtil.existInSet(userAttentionQuestions, qid);
        return hasAttention;
    }


    @Override
    public Long getAnswerStars(Integer qid, Integer aid){
        //问题回答redis键名
        Object questionAnswersRedisKey = BaseUtil.getRedisQuestionAnswersKey(qid);
        //获取问题点赞数
        return redisUtil.getZsetKeyValue(questionAnswersRedisKey, aid).longValue();
    }

}
