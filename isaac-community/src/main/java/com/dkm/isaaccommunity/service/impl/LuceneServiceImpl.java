package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.dao.AnswerDao;
import com.dkm.isaaccommunity.dao.QuestionDao;
import com.dkm.isaaccommunity.result.BaseResult;
import com.dkm.isaaccommunity.result.IResult;
import com.dkm.isaaccommunity.service.LuceneService;
import com.dkm.isaaccommunity.service.RedisService;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.BaseUtil;
import com.dkm.isaaccommunity.util.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Service
public class LuceneServiceImpl implements LuceneService {

    //指定索引目录
    private static Directory directory = null;
    //分词对象
    private static Analyzer analyzer = null;
    //指定索引处理IndexWriter对象
    private static IndexWriter indexWriter = null;
    //索引检索对象
    private static IndexSearcher indexSearcher = null;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserService userService;

    static {
        if(null == directory){
            try {
                String luceneIndexPath = FileUtil.getImgDirFile(GlobalConstant.LUCENE_INDEX_PATH).getAbsolutePath();
                directory = FSDirectory.open(Paths.get(luceneIndexPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(null == analyzer){
            //指定分词器
            analyzer = new StandardAnalyzer();
        }
    }

    /**
     * 获取写索引操作IndewWriter对象
     *
     * @return
     */
    public  IndexWriter getIndexWriter(){
        if(null == indexWriter || !indexWriter.isOpen()){
            try {
                IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);

                //指定索引处理IndexWriter对象
                indexWriter = new IndexWriter(directory, writerConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return indexWriter;
    }

    /**
     * 获取读取索引信息IndexSearch对象
     *
     * @return
     */
    public IndexSearcher getIndexSearcher(){
        if(null == indexSearcher){
            try {
                IndexReader indexReader = DirectoryReader.open(directory);
                indexSearcher = new IndexSearcher(indexReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return indexSearcher;
    }

    /**
     * 添加对象索引
     *
     * @param   object
     * @return
     */
    @Override
    public IResult addIndex(Object object){
        if(null == object){
            return BaseResult.FAILTURE;
        }

        try {
            //创建Document对象
            Document document = getDocumentByJavaBean(object);
            //添加文档对象
            getIndexWriter().addDocument(document);
            //提交
            getIndexWriter().commit();

            //关闭索引处理对象
            // super.getIndexWriter().close();

            return BaseResult.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return BaseResult.FAILTURE;
        }
    }


    /**
     * 解析JavaBean对象获取对应的索引文档对象
     *
     * @param object   要被索引的Java Bean对象
     *
     * @return {Document}
     */
    protected Document getDocumentByJavaBean(Object object){
        if(null == object){
            return null;
        }

        Document document = new Document();
        Class objClass = object.getClass();
        String type = "";

        //判断对象类型，决定给所添加的索引域名称前缀
        if(object instanceof Question){
            type = "q_";
        }else if(object instanceof Answer){
            type = "a_";
        }

        //获取对象属性名称以及值
        Map<Object, Object> objFieldsMap = BaseUtil.getObjFieldMap(object);
        if(null == objFieldsMap || 0 == objFieldsMap.size()){
            return null;
        }

        //将title和text以及问题、回答ID属性及值索引
        Set<Map.Entry<Object, Object>> entrySet = objFieldsMap.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet){
            if(entry.getKey().toString().contains("id") || entry.getKey().toString().contains("title") ||
                    entry.getKey().toString().contains("description") || entry.getKey().toString().contains("content")){
                document.add(new TextField(type + entry.getKey().toString(), entry.getValue().toString(), Field.Store.YES));
            }
        }

        return document;
    }

    /**
     * 检索索引获取结果
     *
     * @param words 检索关键词
     * @return
     */
    @Override
    public List<Question> getSearchResult(String words) {
        if(BaseUtil.isNullOrEmpty(words)){
            return null;
        }

        List<Question> result = new ArrayList<>();

        try {
            //检索单个字段
            //Query query = new TermQuery(new Term("q_title", words));

            //检索多个字段
            String[] queryWords = new String[]{words, words, words};
            String[] fields = {"q_title", "q_description", "a_content"};
            Query query = MultiFieldQueryParser.parse(queryWords, fields, analyzer);

            IndexSearcher indexSearcher = this.getIndexSearcher();

            //共检索到topDocs.totalHits条数据！
            TopDocs topDocs = indexSearcher.search(query, 100);
            System.out.println("共检索到" + topDocs.totalHits + "条数据");

            //用于判断是否同一条问题
            HashSet<Integer> flag_qid = new HashSet<>();
            HashSet<Integer> flag_aid = new HashSet<>();

            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                Document document = indexSearcher.doc(scoreDoc.doc);

                String title = document.get("q_title");
                String description = document.get("q_description");
                String content = document.get("a_content");

                if(!BaseUtil.isNullOrEmpty(title)){
                    Integer qid = Integer.parseInt(document.get("q_id"));

                    //判断是否为同一个问题
                    if(flag_qid.contains(qid)){
                        continue;
                    }

                    //获取问题对象
                    Question question = getSearcherQuestionByQID(qid);

                    if(question != null){
                        flag_qid.add(question.getId());
                        flag_aid.add(question.getHotanswerid());
                        result.add(question);
                        System.out.println("qid:" + document.get("q_id"));
                        System.out.println("title:" + document.get("q_title"));
                    }
                }

                if(!BaseUtil.isNullOrEmpty(description)){
                    Integer qid = Integer.parseInt(document.get("q_id"));

                    //判断是否为同一个问题
                    if(flag_qid.contains(qid)){
                        continue;
                    }
                    //获取问题对象
                    Question question = getSearcherQuestionByQID(qid);
                    if(question != null){
                        flag_qid.add(question.getId());
                        flag_aid.add(question.getHotanswerid());
                        result.add(question);
                        System.out.println("qid:" + document.get("q_id"));
                        System.out.println("desc:" + document.get("q_description"));
                    }
                }

                if(!BaseUtil.isNullOrEmpty(content)){
                    Integer aid = Integer.parseInt(document.get("a_id"));

                    //判断是否同一个回答
                    if(flag_aid.contains(aid)){
                        continue;
                    }
                    //获取问题对象
                    Question question = getSearcherQuestionByAID(aid);
                    if(question != null){
                        flag_qid.add(question.getId());
                        flag_aid.add(question.getHotanswerid());
                        result.add(question);
                        System.out.println("aid:" + document.get("a_id"));
                        System.out.println("answerContent:" + document.get("a_content"));
                    }
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据qid获取问题并设置问题的热门答案
     *
     * @param qid 问题ID
     * @return
     */
    private Question getSearcherQuestionByQID(Integer qid){
        Question question =  questionDao.selectQuestionById(qid);
        if(question ==null){
            return null;
        }

        //设置提问者头像
        User user = question.getUser();
        user.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + userService.getUser(user.getId()).getImgName());
        user.setUserName(userService.getUser(user.getId()).getUserName());
        question.setUser(user);

        //点赞数最多的回答
        ZSetOperations.TypedTuple<Object> hotAnswer = redisService.getQuestionMostStarAnswer(question);
        if(null == hotAnswer){
            question.setHotanswer(" ");
            question.setHotuserheadimg(user.getImgAddr());
            question.setHotusername(user.getUserName());
            return question;
        }
        //获取点赞数最多回答详情
        Answer answer = answerDao.getAnswer((Integer) hotAnswer.getValue());

        //设置问题点赞最多回答信息
        question.setAnswerInfo(answer);
        question.setHotstar(hotAnswer.getScore().intValue());

        //设置点赞数最多回答人员头像
        question.setHotuserheadimg(GlobalConstant.USER_HEAD_PRE_ADDR + userService.getUser(question.getHotuserid()).getImgName());


        return question;
    }


    /**
     * 根据aid获取问题并设置该回答为答案
     *
     * @param aid 回答ID
     * @return
     */
    private Question getSearcherQuestionByAID(Integer aid){
        Answer answer = answerDao.getAnswer(aid);
        if(answer == null){
            return null;
        }
        Question question = questionDao.selectQuestionById(answer.getQuestionID());
        question.setAnswerInfo(answer);
        question.setHotstar(answer.getStar());
        User user = question.getUser();
        user.setImgAddr(GlobalConstant.USER_HEAD_PRE_ADDR + user.getImgName());
        question.setUser(user);
        question.setHotuserheadimg(GlobalConstant.USER_HEAD_PRE_ADDR + answer.getUser().getImgName());
        return question;
    }

}
