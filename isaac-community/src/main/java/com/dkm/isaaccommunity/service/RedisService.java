package com.dkm.isaaccommunity.service;

import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.bean.User;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * Redis处理逻辑定义接口
 */
public interface RedisService {


    /**
     * 缓存已注册用户手机号信息
     *
     * @param   user   注册用户
     */
    void saveHasRegisterPhone(User user);


    /**
     * 判断指定手机号用户是否已注册
     *
     * @param   phone   手机号
     * @return
     */
    boolean validatePhoneHasRegister(String phone);

    /**
     * 新增问题，注册到Redis
     *
     * @param question
     */
    void saveQuestionInfo(Question question);

    /**
     * 获取当前时刻热门问题的总数量
     *
     * @return
     */
    Long getHotQuestionsCount();

    /**
     * 从redis缓存中获取最热门的10条问题
     *
     * @param   page   分页查询页码
     * @param   token  缓存标识
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> getHotQuestionsFromRedis(int page, String token);

    /**
     * 获取问题点赞数最多的回答
     *
     * @param question
     * @return
     */
    ZSetOperations.TypedTuple<Object> getQuestionMostStarAnswer(Question question);

    /**
     * 获取最新的10个问题
     *
     * @return
     */
    Set<Object> getNewestQuestions();
//
    /**
     * 获取指定时刻之前最新问题数量
     *
     * @param time
     * @return
     */
    Long getNewestQuestionsCountByTime(long time);
//
    /**
     * 查询指定时间内按照索引顺序最新的问题
     *
     * @param   page    分页查询页码
     * @param   time    查询时刻
     * @return
     */
    Set<Object> getNewestQuestionsByIndex(int page, long time);

    /**
     * 获取用户提问的所有问题的数量
     *
     * @param userId
     * @return
     */
    Long getUserQuestionsCount(Integer userId);

    /**
     * 新增回答后保存相关信息
     *
     * @param answer 新增的问题回答
     */
    void saveAnswerInfo(Answer answer);

    /**
     * 获取问题回答总数
     *
     * @param   qid  问题ID
     * @return
     */
    Long getQuestionAnswersCount(Integer qid);

    /**
     * 获取用户收藏回答总数
     */
    Long getUserCollectionCount();


    /**
     * 获取用户关注问题总数
     */
    Long getUserAttentionCount();

    /**
     * 对回答点赞操作处理
     *
     * @param   aid  回答ID
     * @return
     */
    void staredAnswer(Integer qid, Integer aid);

    /**
     * 对回答收藏操作处理
     *
     * @param   aid  回答ID
     * @return
     */
    void collectAnswer(Integer aid);

    /**
     * 对问题关注操作处理
     *
     * @param   qid  问题ID
     * @return
     */
    void attentionQuestion(Integer qid);

    /**
     * 判断用户是否对当前回答点赞过
     *
     * @param   aid  回答ID
     * @return
     */
    boolean hasStaredAnswer(Integer aid);

    /**
     * 判断用户是否对当前回答收藏过
     *
     * @param   aid  回答ID
     * @return
     */
    boolean hasCollectAnswer(Integer aid);

    /**
     * 判断用户是否对当前问题关注过
     *
     * @param   qid  问题ID
     * @return
     */
    boolean hasAttentionQuestion(Integer qid);

    /**
     * 获取回答点赞数
     *
     * @param qid   问题ID
     * @param aid   回答ID
     * @return
     */
    Long getAnswerStars(Integer qid, Integer aid);

}
