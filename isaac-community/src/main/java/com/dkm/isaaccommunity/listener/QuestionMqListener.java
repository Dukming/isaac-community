package com.dkm.isaaccommunity.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dkm.isaaccommunity.bean.Question;
import com.dkm.isaaccommunity.service.LuceneService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class QuestionMqListener {

    @Autowired
    private LuceneService luceneService;

    @RabbitHandler
    @RabbitListener(queues = "queue_index_question")
    public void onMessage(Message message) {
        try {
            String questionMessage = new String(message.getBody(), "UTF-8");
            Question question = JSONObject.parseObject(questionMessage, new TypeReference<Question>(){});
            luceneService.addIndex(question);
            System.out.println("add question index");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
