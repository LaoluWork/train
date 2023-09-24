package com.laolu.train.business.rabbitMQ;

import com.alibaba.fastjson.JSON;
import com.laolu.train.business.domain.ConfirmOrder;
import com.laolu.train.business.mapper.ConfirmOrderMapper;
import com.laolu.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Component
public class QueueUtil {
    static private final int QUEUESCOUNT = 4;
    /**
     * map中存储的是车次号对应的队列索引
     */
    static private final HashMap<String, Integer> map = new HashMap<>();
    volatile static private Integer nextId = -1;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private ConfirmOrderService confirmOrderService;

    public Integer getQueueId(String trainCode) {
        if (!map.containsKey(trainCode)) {
            // 该此车次的车票信息第一次需要决定进入下一个队列
            synchronized (QueueUtil.class) {
                if (!map.containsKey(trainCode)) {
                    nextId = ++nextId % QUEUESCOUNT;
                    map.put(trainCode, nextId);
                    return nextId + 1;
                }
            }
        }
        return map.get(trainCode) + 1;
    }


    @Transactional
    public void consume(String message, int queueId) throws Exception{
        ConfirmOrder confirmOrder = JSON.parseObject(message, ConfirmOrder.class);
        int count = confirmOrderMapper.insert(confirmOrder);
        confirmOrderService.doConfirm(confirmOrder);
        System.out.println("队列{ " + queueId + " }成功消费了一条订单。。！！！，影响值：【" + count + "】");

    }
}
