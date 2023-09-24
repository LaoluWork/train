package com.laolu.train.business.rabbitMQ;

import com.alibaba.fastjson.JSON;
import com.laolu.train.business.domain.ConfirmOrder;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private QueueUtil queueUtil;

    //发送日志消息
    public void publish(ConfirmOrder confirmOrder) {
        String message = JSON.toJSONString(confirmOrder);
        Integer queueId = queueUtil.getQueueId(confirmOrder.getTrainCode());
        // 把雪花算法生成的唯一id作为消息的id，在后期可以避免重复消费
        rabbitTemplate.convertAndSend("train.order.exchange",
                "order.key_" + queueId, message, new CorrelationData(confirmOrder.getId().toString()));
        System.out.println("成功在{ " + queueId + " }发布了一条订单。。！！！");
    }
}