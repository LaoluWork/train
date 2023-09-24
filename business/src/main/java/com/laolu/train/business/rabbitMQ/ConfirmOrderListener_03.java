package com.laolu.train.business.rabbitMQ;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderListener_03 {

    @Autowired
    private QueueUtil queueUtil;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "train.order.queue_03",
                            durable = "true",
                            arguments = {
                                    @Argument(
                                            name = "x-dead-letter-exchange",
                                            value = "train.errorExchange"
                                    ),
                                    @Argument(
                                            name = "x-dead-letter-routing-key",
                                            value = "order.consumeError"
                                    )
                            }
                    ),
                    exchange = @Exchange(
                            value = "train.order.exchange",
                            type = ExchangeTypes.DIRECT
                    ),
                    key = "order.key_3"
            )
    )
    public void consume(String dataJson, Channel channel, Message message) throws Exception {
        System.out.println("监听消费中。。。");
        try {
            queueUtil.consume(dataJson, 3);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            System.out.println("消费者线程消费失败，将消息拒绝，放进死信队列");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
