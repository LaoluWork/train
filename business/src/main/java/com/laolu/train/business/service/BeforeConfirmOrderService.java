package com.laolu.train.business.service;

import cn.hutool.core.date.DateTime;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.laolu.train.business.domain.ConfirmOrder;
import com.laolu.train.business.enums.ConfirmOrderStatusEnum;
import com.laolu.train.business.rabbitMQ.ConfirmOrderPublisher;
import com.laolu.train.business.req.ConfirmOrderDoReq;
import com.laolu.train.business.req.ConfirmOrderTicketReq;
import com.laolu.train.common.context.LoginMemberContext;
import com.laolu.train.common.exception.BusinessException;
import com.laolu.train.common.exception.BusinessExceptionEnum;
import com.laolu.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    @Resource
    private ConfirmOrderPublisher confirmOrderPublisher;


    @Transactional
    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    public Long beforeDoConfirm(ConfirmOrderDoReq req) {
        Long id = null;
        try {
            // 根据前端传值，加入排队人数
            for (int i = 0; i < req.getLineNumber() + 1; i++) {
                req.setMemberId(LoginMemberContext.getId());

                Date date = req.getDate();
                String trainCode = req.getTrainCode();
                String start = req.getStart();
                String end = req.getEnd();
                List<ConfirmOrderTicketReq> tickets = req.getTickets();

                // 初始化订单表，发送到mq中
                DateTime now = DateTime.now();
                ConfirmOrder confirmOrder = new ConfirmOrder();
                confirmOrder.setId(SnowUtil.getSnowflakeNextId());
                confirmOrder.setCreateTime(now);
                confirmOrder.setUpdateTime(now);
                confirmOrder.setMemberId(req.getMemberId());
                confirmOrder.setDate(date);
                confirmOrder.setTrainCode(trainCode);
                confirmOrder.setStart(start);
                confirmOrder.setEnd(end);
                confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
                confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
                confirmOrder.setTickets(JSON.toJSONString(tickets));
                confirmOrder.setLogId(MDC.get("LOG_ID"));

                // 将消息发布到mq中
                confirmOrderPublisher.publish(confirmOrder);
                id = confirmOrder.getId();
            }
        } catch (Exception e) {
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
        }
        return id;
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
