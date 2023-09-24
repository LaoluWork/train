package com.laolu.train.business.rabbitMQ;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.laolu.train.business.domain.ConfirmOrder;
import com.laolu.train.business.enums.ConfirmOrderStatusEnum;
import com.laolu.train.business.mapper.ConfirmOrderMapper;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DeadLetterListener {

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "train.order.error",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = "train.errorExchange",
                            type = ExchangeTypes.DIRECT
                    ),
                    key = "order.consumeError"
            )
    )
    public void consume(String dataJson, Channel channel, Message message) {
        System.out.println("死信队列消费中。。。");
        ConfirmOrder confirmOrder = JSON.parseObject(dataJson, ConfirmOrder.class);
        ConfirmOrder exists = confirmOrderMapper.selectByPrimaryKey(confirmOrder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.FAILURE.getCode());
        if (ObjectUtil.isNull(exists)) {
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrder);
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
