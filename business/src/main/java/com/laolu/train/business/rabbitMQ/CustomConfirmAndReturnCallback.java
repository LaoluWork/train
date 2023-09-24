package com.laolu.train.business.rabbitMQ;

import com.alibaba.fastjson.JSON;
import com.laolu.train.business.domain.ConfirmOrder;
import com.laolu.train.business.enums.ConfirmOrderStatusEnum;
import com.laolu.train.business.mapper.ConfirmOrderMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomConfirmAndReturnCallback  implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    Logger LOG = LoggerFactory.getLogger(CustomConfirmAndReturnCallback.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @PostConstruct
    public void init() {
        //指定 ConfirmCallback
        rabbitTemplate.setConfirmCallback(this);
        //指定 ReturnsCallback
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String error) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (!ack) {
            LOG.error("发送订单消息到交换机失败, 订单id为:{}", id);
            try {
                String dataJson = new String(correlationData.getReturned().getMessage().getBody());
                ConfirmOrder confirmOrder = JSON.parseObject(dataJson, ConfirmOrder.class);
                // 将该订单的状态修改为失败，同时插入数据库
                confirmOrder.setStatus(ConfirmOrderStatusEnum.FAILURE.getCode());
                confirmOrderMapper.insert(confirmOrder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        try {
            String dataJson = new String(returnedMessage.getMessage().getBody());
            ConfirmOrder confirmOrder = JSON.parseObject(dataJson, ConfirmOrder.class);
            // 将该订单的状态修改为失败，同时插入数据库
            confirmOrder.setStatus(ConfirmOrderStatusEnum.FAILURE.getCode());
            LOG.error("交换机发送订单消息到队列失败, 订单id为:{}", confirmOrder.getId());
            confirmOrderMapper.insert(confirmOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

