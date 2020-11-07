package com.dkm.isaaccommunity.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dkm.isaaccommunity.bean.Answer;
import com.dkm.isaaccommunity.service.LuceneService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component

public class AnswerMqListener {

    @Autowired
    private LuceneService luceneService;

    @RabbitHandler
    @RabbitListener(queues = "queue_index_answer")
    public void onMessage(Message message) {
        try {
            String answerMessage = new String(message.getBody(), "UTF-8");
            System.out.println("message : " + message.getBody());
            Answer answer = JSONObject.parseObject(answerMessage, new TypeReference<Answer>(){});
            luceneService.addIndex(answer);
            System.out.println("add answer index");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
